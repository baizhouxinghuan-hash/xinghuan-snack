package xiaobaisnack.crawler.url.manager;

public class UrlStateMachine {

    public enum State {
        NEW,        // 新URL
        QUEUED,     // 已入队
        FETCHING,   // 抓取中
        DONE,       // 已完成
        FAILED      // 失败
    }

}