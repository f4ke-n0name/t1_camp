package com.f4ken0name.github.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BlackListImpl implements BlackList {
    private final ArrayList<String> blacklist;
    public BlackListImpl(ArrayList<String> blacklist) {
        this.blacklist = blacklist;
    }
    public void add(String email) {
        blacklist.add(email);
    }
    public boolean contains(String email) {
        return blacklist.contains(email);
    }
}
