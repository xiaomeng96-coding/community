package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhiHao
 * @since : 2022/4/19
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";

    private final TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
            ) {
            String keyWord;
            while ((keyWord = reader.readLine()) != null) {
                this.addKeyWord(keyWord);
            }
        }catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char ch = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(ch);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(ch, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    public String filter (String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int beginIndex = 0;
        // 指针3
        int position = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (position < text.length()) {
            char ch = text.charAt(position);
            // 跳过符号
            if (isSymbol(ch)) {
                // 若指针1指向根节点, 将刺符号计入结果，让指针2向下走一步
                if (tempNode == rootNode) {
                    stringBuilder.append(ch);
                    beginIndex++;
                }
                // 指针3向前走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(ch);
            if (tempNode == null) {
                // 以beginIndex开始的字符串不是敏感词
                stringBuilder.append(text.charAt(beginIndex));
                // 进入下一个位置
                position = ++beginIndex;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词，替换字符
                stringBuilder.append(REPLACEMENT);
                // 进入下一个位置
                beginIndex = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }
        // 将最后一批字符计入结果
        stringBuilder.append(text.substring(beginIndex));
        return stringBuilder.toString();
    }

    private boolean isSymbol(Character ch) {
        // 0x2E90~0x9FFF东亚文字
        return CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

    private static class TrieNode {
        private boolean isKeywordEnd;
        // 子节点（key是下级节点字符，value是下级节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeywordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            this.isKeywordEnd = keyWordEnd;
        }

        public void addSubNode(Character ch, TrieNode subNode) {
            subNodes.put(ch, subNode);
        }

        public TrieNode getSubNode(Character ch) {
            return subNodes.get(ch);
        }
    }
}
