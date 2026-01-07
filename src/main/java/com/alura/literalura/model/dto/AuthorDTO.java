package com.alura.literalura.model.dto;

/**
 * Data Transfer Object for Author information from external APIs.
 * Represents author data in a format suitable for API responses and data transformation.
 */
public record AuthorDTO(
    String id,
    String name,
    Integer birthYear,
    Integer deathYear,
    String bio,
    String wikipediaUrl
) {
    
    /**
     * Creates an AuthorDTO with minimal required fields.
     * @param id The external author ID
     * @param name The author name
     */
    public AuthorDTO(String id, String name) {
        this(id, name, null, null, null, null);
    }

    /**
     * Creates an AuthorDTO with birth and death years.
     * @param id The external author ID
     * @param name The author name
     * @param birthYear The birth year
     * @param deathYear The death year
     */
    public AuthorDTO(String id, String name, Integer birthYear, Integer deathYear) {
        this(id, name, birthYear, deathYear, null, null);
    }

    /**
     * Validates if the AuthorDTO contains essential information.
     * @return true if the DTO has a valid name
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Checks if the author is still alive.
     * @return true if death year is null
     */
    public boolean isAlive() {
        return deathYear == null;
    }

    /**
     * Calculates the author's age at death.
     * @return Age at death, or -1 if cannot be calculated
     */
    public int getAgeAtDeath() {
        if (birthYear != null && deathYear != null) {
            return deathYear - birthYear;
        }
        return -1;
    }

    /**
     * Gets the author's lifespan as a formatted string.
     * @return Formatted lifespan (e.g., "1800-1875" or "1800-")
     */
    public String getLifespan() {
        StringBuilder lifespan = new StringBuilder();
        if (birthYear != null) {
            lifespan.append(birthYear);
        } else {
            lifespan.append("?");
        }
        lifespan.append("-");
        if (deathYear != null) {
            lifespan.append(deathYear);
        } else {
            lifespan.append("present");
        }
        return lifespan.toString();
    }

    /**
     * Gets the author's century based on birth year.
     * @return Century as a string (e.g., "19th century"), or "Unknown"
     */
    public String getCentury() {
        if (birthYear == null) {
            return "Unknown";
        }
        
        int century = (birthYear - 1) / 100 + 1;
        String suffix = switch (century % 100) {
            case 11, 12, 13 -> "th";
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        
        return century + suffix + " century";
    }

    /**
     * Checks if the author has a Wikipedia URL.
     * @return true if Wikipedia URL is provided
     */
    public boolean hasWikipediaUrl() {
        return wikipediaUrl != null && !wikipediaUrl.trim().isEmpty();
    }

    /**
     * Checks if the author has a biography.
     * @return true if biography is provided
     */
    public boolean hasBiography() {
        return bio != null && !bio.trim().isEmpty();
    }

    /**
     * Gets a short biography excerpt (first 100 characters).
     * @return Biography excerpt, or empty string if no biography
     */
    public String getBiographyExcerpt() {
        if (!hasBiography()) {
            return "";
        }
        
        String excerpt = bio.trim();
        if (excerpt.length() > 100) {
            excerpt = excerpt.substring(0, 97) + "...";
        }
        return excerpt;
    }

    /**
     * Gets a formatted representation of the author for display.
     * @return Formatted string with key author information
     */
    @Override
    public String toString() {
        return String.format(
            "AuthorDTO{id='%s', name='%s', lifespan='%s', age='%s'}",
            id, name, getLifespan(), 
            getAgeAtDeath() > 0 ? getAgeAtDeath() + " years" : "Unknown"
        );
    }
}
