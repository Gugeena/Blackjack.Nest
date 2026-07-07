package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventCacheService
{
    @Autowired
    private EventCacheRepository eventCacheRepository;

    protected void save(EventCache eventCache)
    {
        eventCacheRepository.save(eventCache);
    }

    protected boolean checkBySlug(String slug)
    {
        return eventCacheRepository.findByEventSlug(slug).isPresent();
    }

    protected EventCache findBySlug(String slug)
    {
        return eventCacheRepository.findByEventSlug(slug).orElse(new EventCache());
    }
}
