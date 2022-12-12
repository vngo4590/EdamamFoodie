module au.edu.sydney.soft3202.majorproject {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.media;
  requires okhttp3;
  requires retrofit2;
  requires retrofit2.converter.gson;
  requires retrofit2.converter.scalars;
  requires com.google.gson;
  requires java.sql;
  requires org.xerial.sqlitejdbc;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;

  opens au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients;
  opens au.edu.sydney.soft3202.majorproject.model.entity.edamam.input;
  opens au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;
  opens au.edu.sydney.soft3202.majorproject.model.api.edamam;
  opens au.edu.sydney.soft3202.majorproject.model.api.edamam.allergen;
  opens au.edu.sydney.soft3202.majorproject.model.api.pastebin;
  opens au.edu.sydney.soft3202.majorproject.model.api.reddit;
  opens au.edu.sydney.soft3202.majorproject.model.entity.error;
  opens au.edu.sydney.soft3202.majorproject.model.entity.edamam.adapter;
  opens au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission;
  opens au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization;
  opens au.edu.sydney.soft3202.majorproject to
      javafx.fxml;
  opens au.edu.sydney.soft3202.majorproject.controller to
      javafx.fxml;

  exports au.edu.sydney.soft3202.majorproject;
}
