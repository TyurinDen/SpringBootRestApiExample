package com.websystique.springboot.testPackage;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class JsonTree {
    private List<JsonTree> nodes = new ArrayList<>();
    private List<JsonObject> values = new ArrayList<>();
    private String nodeName;

    public void addNode(JsonTree tree, String nodeName) {
        tree.setNodeName(nodeName);
        nodes.add(tree);
    }

    public void addValue(JsonObject jsonObject) {
        values.add(jsonObject);
    }

    public List<JsonTree> getNodes() {
        return nodes;
    }

    public List<JsonObject> getValues() {
        return values;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
