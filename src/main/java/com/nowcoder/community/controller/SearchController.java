package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.SearchDiscussResult;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zhiHao
 * @since : 2022/5/30
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search (String keyword, Page page, Model model) {
        SearchDiscussResult searchDiscussResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> result = new ArrayList<>();
        List<DiscussPost> postList = searchDiscussResult.getDiscussPosts();
        if ( !CollectionUtils.isEmpty(postList)) {
            for (DiscussPost post : postList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.selectUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                result.add(map);
            }
            model.addAttribute("discussPosts", result);
            model.addAttribute("keyWord", keyword);
        }
        page.setPath("/search?keyword=" + keyword);
        page.setRowCounts(CollectionUtils.isEmpty(postList) ? 0 : searchDiscussResult.getTotalSize());
        return "/site/search";
    }
}
