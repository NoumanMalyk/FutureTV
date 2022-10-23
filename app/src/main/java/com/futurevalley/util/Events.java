package com.futurevalley.util;

import com.futurevalley.item.ItemComment;

import java.util.ArrayList;

public class Events {
    public static class Comment {
        private ArrayList<ItemComment> itemComments;
        private String postType;

        public ArrayList<ItemComment> getItemComments() {
            return itemComments;
        }

        public void setItemComments(ArrayList<ItemComment> itemComments) {
            this.itemComments = itemComments;
        }

        public String getPostType() {
            return postType;
        }

        public void setPostType(String postType) {
            this.postType = postType;
        }
    }

    public static class FullScreen {
        private boolean isFullScreen = false;

        public boolean isFullScreen() {
            return isFullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            isFullScreen = fullScreen;
        }
    }
}
