package com.cj.common.safe;

import com.jfinal.token.ITokenCache;
import com.jfinal.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * token缓存实现接口
 * @author THINKPAD
 */
public class TokenCacheImpl implements ITokenCache {

    private List<Token> tokenList = new ArrayList<>();

    /**
     * 将token放到tokenList中
     * @param token
     */
    @Override
    public void put(Token token) {
        tokenList.add(token);
    }

    /**
     * 将token从tokenList移出
     * @param token
     */
    @Override
    public void remove(Token token) {
        tokenList.remove(token);
    }

    /**
     * 用于判断token是否存在于tokenList中
     * @param token
     * @return
     */
    @Override
    public boolean contains(Token token) {
        return tokenList.contains(token);
    }

    /**
     * 返回tokenList
     * @return
     */
    @Override
    public List<Token> getAll() {
        return tokenList;
    }
}
