package com.example.API.Peche.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.API.Peche.repository.CatchRepository;

@Service
public class CatchService {

    @Autowired
    private CatchRepository catchRepository;

    @Autowired
    private UserService userService;

    
}