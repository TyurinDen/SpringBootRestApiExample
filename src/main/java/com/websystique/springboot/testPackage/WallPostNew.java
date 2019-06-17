package com.websystique.springboot.testPackage;

import java.util.List;

//TODO удалить, этот класс не понадобится
public class WallPostNew {
    private int id;
    private int fromId;
    private int ownerId;
    private int date;
    private int markedAsAds;
    private String postType;
    private String text;
    private boolean canEdit;
    private int cteatedBy;
    private boolean canDelete;
    private List<Comments> comments;
    private int groupId;

    class Comments {
        private int count;
    }
}
