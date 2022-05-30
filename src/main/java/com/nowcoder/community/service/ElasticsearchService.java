package com.nowcoder.community.service;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.SearchDiscussResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhiHao
 * @since : 2022/5/30
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    public void saveDiscussPost (DiscussPost post) {
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost (int id) {
        discussPostRepository.deleteById(id);
    }

    public SearchDiscussResult searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> searchHits = elasticTemplate.search(build, DiscussPost.class);
        List<DiscussPost> result = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost post = hit.getContent();
            // 处理高亮显示
            List<String> highLightTitle = hit.getHighlightFields().get("title");
            if ( !CollectionUtils.isEmpty(highLightTitle)) {
                post.setTitle(highLightTitle.get(0));
            }
            List<String> highLightContent = hit.getHighlightFields().get("content");
            if ( !CollectionUtils.isEmpty(highLightContent)) {
                post.setContent(highLightContent.get(0));
            }
            result.add(post);
        }
        return new SearchDiscussResult(result, (int)searchHits.getTotalHits());
    }
}
