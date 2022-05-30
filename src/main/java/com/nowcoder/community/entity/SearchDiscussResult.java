package com.nowcoder.community.entity;

import lombok.Data;

import java.util.List;

/**
 * @author : zhiHao
 * @since : 2022/5/30
 */
@Data
public class SearchDiscussResult {

    private List<DiscussPost> discussPosts;
    private int totalSize;

    public SearchDiscussResult(List<DiscussPost> discussPosts, int totalSize) {
        this.discussPosts = discussPosts;
        this.totalSize = totalSize;
    }
}
