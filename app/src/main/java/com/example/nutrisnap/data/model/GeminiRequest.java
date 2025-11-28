package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiRequest {
    @SerializedName("contents")
    public List<Content> contents;

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        @SerializedName("inline_data")
        public InlineData inlineData;

        public Part(String text) {
            this.text = text;
        }

        public Part(InlineData inlineData) {
            this.inlineData = inlineData;
        }
    }

    public static class InlineData {
        @SerializedName("mime_type")
        public String mimeType;

        @SerializedName("data")
        public String data;

        public InlineData(String mimeType, String data) {
            this.mimeType = mimeType;
            this.data = data;
        }
    }
}
