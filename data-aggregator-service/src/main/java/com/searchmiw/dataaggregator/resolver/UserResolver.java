package com.searchmiw.dataaggregator.resolver;

import com.searchmiw.dataaggregator.model.HistoryEntry;
import com.searchmiw.dataaggregator.model.User;
import com.searchmiw.dataaggregator.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final HistoryService historyService;
    
    @BatchMapping
    public Map<User, List<HistoryEntry>> searchHistory(List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(
                        user -> user,
                        user -> historyService.getEntriesByUserId(user.getId())
                ));
    }
}
