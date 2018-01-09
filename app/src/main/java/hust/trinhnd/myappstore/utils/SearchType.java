package hust.trinhnd.myappstore.utils;

import java.util.ArrayList;

/**
 * Created by Trinh on 17/12/2017.
 */

public class SearchType {
    public static final String TYPE_TITTLE= "Tiêu đề";
    public static final String TYPE_COURSE= "Học phần";
    public static final String TYPE_USER= "Người dùng";
    public static String[] getSearchType() {
        return new String[]
                {
                        TYPE_TITTLE, TYPE_COURSE, TYPE_USER
                };
    }
}
