package com.rcircle.service.gateway.services;

import com.alibaba.fastjson.JSON;
import com.rcircle.service.gateway.events.sink.FeedBackSink;
import com.rcircle.service.gateway.events.sink.HLSSink;
import com.rcircle.service.gateway.events.sink.NewsSink;
import com.rcircle.service.gateway.events.sink.SmsSink;
import com.rcircle.service.gateway.model.HLSMap;
import com.rcircle.service.gateway.model.Message;
import com.rcircle.service.gateway.utils.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@EnableBinding(value = {NewsSink.class, SmsSink.class, HLSSink.class, FeedBackSink.class})
public class MessageService {
    public int RESULT_FAILED = 0;
    public int RESULT_SUCCESS = 1;
    public int RESULT_UNKNOWN = 2;
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private List<Message> newsList = new ArrayList<>();
    private List<Message> smsList = new ArrayList<>();
    private List<Message> sysList = new ArrayList<>();
    private List<HLSMap> hlsList = new ArrayList<>();
    private Lock newsListLock = new ReentrantLock();
    private Lock smsListLock = new ReentrantLock();
    private Lock hlsListLock = new ReentrantLock();
    private Lock sysListLock = new ReentrantLock();

    @StreamListener(NewsSink.TOPIC)
    public void receiveNews(String string) {
        Message news = JSON.parseObject(string, Message.class);
        news.setType(Message.TYPE_NEWS);
        news.setId(Toolkit.getCurrentTimeInMillis());
        logger.info("Title:{}, author:{}, url:{}, date:{}", news.getTitle(), news.getSender(), news.getContent(), news.getDate());
        newsListLock.lock();
        newsList.add(news);
        newsListLock.unlock();
    }

    @StreamListener(SmsSink.TOPIC)
    public void receiveSms(String str) {
        Message sms = JSON.parseObject(str, Message.class);
        sms.setType(Message.TYPE_SMS);
        sms.setId(Toolkit.getCurrentTimeInMillis());
        logger.info("SEND_UID:{}, RECEIVE_UID:{}, MESSAGE:{}, DATE:{}", sms.getSender(), sms.getReceiver_uid(), sms.getContent(), sms.getDate());
        smsListLock.lock();
        smsList.add(sms);
        smsListLock.unlock();
    }

    @StreamListener(HLSSink.TOPIC)
    public void receiveHLS(String str) {
        HLSMap map = JSON.parseObject(str, HLSMap.class);
        logger.info("LOG_ID:{}, FILE_NAME:{}, RESULT:{}", map.getId(), map.getName(), map.isSuccess());
        hlsListLock.lock();
        hlsList.add(map);
        hlsListLock.unlock();
    }

    @StreamListener(FeedBackSink.TOPIC)
    public void receiveFeedBack(String str) {
        Message feedback = JSON.parseObject(str, Message.class);
        feedback.setType(Message.TYPE_SYS);
        feedback.setId(Toolkit.getCurrentTimeInMillis());
        sysListLock.lock();
        sysList.add(feedback);
        sysListLock.unlock();
    }

    public int checkHLSResult(int logid, String filename) {
        int ret = RESULT_UNKNOWN;
        hlsListLock.lock();
        Iterator<HLSMap> iter = hlsList.iterator();
        while (iter.hasNext()) {
            HLSMap map = iter.next();
            if (map.isPointSameFile(logid, filename)) {
                ret = map.isSuccess() ? RESULT_SUCCESS : RESULT_FAILED;
                break;
            }
        }
        hlsListLock.unlock();
        return ret;
    }

    public void clearAllHLSResultFor(int logid) {
        int ret = RESULT_UNKNOWN;
        hlsListLock.lock();
        Iterator<HLSMap> iter = hlsList.iterator();
        while (iter.hasNext()) {
            HLSMap map = iter.next();
            if (map.getId() == logid) {
                hlsList.remove(map);
            }
        }
        hlsListLock.unlock();
    }

    private List<Message> getNewsList() {
        List<Message> currentList = cloneList(newsList, newsListLock);
        if (currentList.isEmpty() || currentList.size() < 2) {
            Message defaultNews = new Message();
            defaultNews.setTitle("Welcome to Simple & Living");
            defaultNews.setContent("#");
            currentList.add(defaultNews);
            defaultNews = new Message();
            defaultNews.setTitle("You can post your opinion");
            defaultNews.setContent("#");
            currentList.add(defaultNews);
        }
        return currentList;
    }

    public List<Message> getMessageList(int type) {
        List<Message> list = null;
        Message message = null;
        switch (type) {
            case Message.TYPE_NEWS:
            case Message.TYPE_NEWS_IMP:
                list = cloneList(newsList, newsListLock);
                break;
            case Message.TYPE_SMS:
                list = cloneList(smsList, smsListLock);
                break;
            case Message.TYPE_SYS:
            case Message.TYPE_SYS_IMP:
                list = cloneList(sysList, sysListLock);
                break;
        }
        return list;
    }

    private <T> List<T> cloneList(List<T> list, Lock lock) {
        List<T> newList = new ArrayList<>();
        if (list.size() == 0) {
            return newList;
        }
        lock.lock();
        Iterator<T> iter = list.iterator();
        while (iter.hasNext()) {
            newList.add(iter.next());
        }
//        list.clear();
        lock.unlock();
        return newList;
    }
}
