package com.example.githubtrailblazer.ghapi;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing the sorting options exported by the GitHub API.
 */
public enum SortByOptions {
    Best_Match("Best Match"),
    Most_Stars("Most Stars"),
    Fewest_Stars("Fewest Stars"),
    Most_Forks("Most Forks"),
    Fewest_Forks("Fewest Forks"),
    Recently_Updated("Recently Updated"),
    Least_Recently_Updated("Least Recently Updated");

    private String text;

    SortByOptions(String s) {
        this.text = s;
    }

    public static Optional<SortByOptions> fromString(String text) {
        return Arrays.stream(values())
                .filter(sbo -> sbo.text.equalsIgnoreCase(text))
                .findFirst();
    }

    /*
    From the docs it seems like there are 7 kinds of orderings supported by the GitHub API

    1. Best Match (ordering by default)
    2. Most Stars (`sort:stars-desc`)
    3. Fewest Starts (`sort:stars-asc`)
    4. Most Forks (`sort:forks-desc`)
    5. Fewest Forks (`sort:forks-asc`)
    6. Recently Updated (`sort:updated-desc`)
    7. Least Recently Updated (`sort:updated-asc`)

    TODO: Integrate into the search query.
     */
}
