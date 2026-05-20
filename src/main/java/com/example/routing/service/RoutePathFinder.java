package com.example.routing.service;

import com.example.routing.exception.RouteNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RoutePathFinder {

    public List<String> findShortestPath(Map<String, List<String>> borderGraph, String originCode, String destinationCode) {
        if (originCode.equals(destinationCode)) {
            return List.of(originCode);
        }

        SearchState searchState = initSearch(originCode);
        while (!searchState.queue().isEmpty()) {
            String current = searchState.queue().removeFirst();
            if (visitNeighbors(borderGraph, current, destinationCode, searchState)) {
                return buildPath(searchState.previous(), originCode, destinationCode);
            }
        }

        throw new RouteNotFoundException("No land route found");
    }

    private SearchState initSearch(String originCode) {
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previous = new HashMap<>();

        queue.add(originCode);
        visited.add(originCode);

        return new SearchState(queue, visited, previous);
    }

    private boolean visitNeighbors(
            Map<String, List<String>> borderGraph,
            String current,
            String destinationCode,
            SearchState searchState
    ) {
        for (String neighbor : borderGraph.getOrDefault(current, List.of())) {
            if (searchState.visited().contains(neighbor)) {
                continue;
            }
            searchState.visited().add(neighbor);
            searchState.previous().put(neighbor, current);

            if (neighbor.equals(destinationCode)) {
                return true;
            }
            searchState.queue().addLast(neighbor);
        }
        return false;
    }

    private List<String> buildPath(Map<String, String> previous, String origin, String destination) {
        List<String> route = new ArrayList<>();
        String cursor = destination;

        route.add(cursor);
        while (!cursor.equals(origin)) {
            cursor = previous.get(cursor);
            if (cursor == null) {
                throw new RouteNotFoundException("No land route found");
            }
            route.add(cursor);
        }

        Collections.reverse(route);
        return route;
    }

    private record SearchState(Deque<String> queue, Set<String> visited, Map<String, String> previous) {
    }
}
