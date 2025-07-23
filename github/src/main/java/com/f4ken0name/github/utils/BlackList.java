package com.f4ken0name.github.utils;

import org.springframework.stereotype.Service;

@Service
public interface BlackList {
    void add(String email);
    boolean contains(String email);
}
