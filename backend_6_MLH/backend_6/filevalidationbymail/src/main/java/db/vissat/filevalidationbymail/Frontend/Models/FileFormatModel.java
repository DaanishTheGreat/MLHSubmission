package db.vissat.filevalidationbymail.Frontend.Models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class FileFormatModel {
    @JsonProperty
    private Map<String, Map<String, Map<String, Field>>> categories = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Map<String, Map<String, Field>>> getCategories() {
        return categories;
    }

    @JsonAnySetter
    public void setCategory(String name, Map<String, Map<String, Field>> category) {
        this.categories.put(name, category);
    }

    public static class Field {
        @JsonProperty("Type")
        private String type;

        @JsonProperty("Title")
        private String title;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
