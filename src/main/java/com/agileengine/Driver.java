package com.agileengine;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Driver {
    private static Logger LOGGER = LoggerFactory.getLogger(Driver.class);
    private static final String TAG_SPLITTER = " > ";
    private static final String ID_KEY = "id";
    private static final String ROOT_ELEMENT_VALUE = "#root";

    public static void main(String[] args) {
        String resourcePath = args[0];
        String resourcePath2 = args[1];
        String targetElementId  = "make-everything-ok-button";
        if (args.length >= 3) {
             targetElementId = args[2];
        }

        File originalFile = new File(resourcePath);
        File diffFile = new File(resourcePath2);

        Element button = JsoupFinder.findElementById(originalFile, targetElementId).orElse(null);
        if (button != null) {
            List<Element> elements = searchElementsByAttributes(button, diffFile);
            Map<Integer, List<Element>> mostFrequentElements = getMostFrequentElements(elements);
            printElementsPath(mostFrequentElements);
        }

    }

    private static void printElementsPath(Map<Integer, List<Element>> elements) {
        Map.Entry<Integer, List<Element>> elementEntry = elements.entrySet().iterator().next();
        for (Element el : elementEntry.getValue()) {
            LOGGER.info("Path to element: {}", getPath(el));
            LOGGER.info("{} attributes are equal with original object", elementEntry.getKey());
        }
    }

    private static Map<String, List<Element>> getFrequencyMap(List<Element> elements) {
        Map<String, List<Element>> frequencyMap = elements.parallelStream()
                .collect(Collectors.groupingBy(Element::outerHtml))
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(el -> el.getValue().size())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
                        e2, LinkedHashMap::new));

        return frequencyMap;
    }

    private static List<Element> searchElementsByAttributes(Element button, File file) {
        List<Element> elements = new ArrayList<>();
        button.attributes().asList().forEach(attribute -> {
            if (!attribute.getKey().equals(ID_KEY)) {
                String cssQueryDiff = getCssQuery(button.tagName(), attribute);
                JsoupFinder.findElementsByQuery(file, cssQueryDiff).ifPresent(elements::addAll);
            }
        });
        return elements;
    }

    private static Map<Integer, List<Element>> getMostFrequentElements(List<Element> elements) {
        Map<String, List<Element>> elementsMap = getFrequencyMap(elements);
        List<Element> elementsList = new ArrayList<>();
        Map<Integer, List<Element>> resultMap = new HashMap<>();
        int maxSize = 0;
        for (Map.Entry<String, List<Element>> entry : elementsMap.entrySet()) {
            List<Element> value = entry.getValue();
            if (maxSize == 0) {
                elementsList.add(value.get(0));
                maxSize = value.size();
            } else {
                if (maxSize == value.size()) {
                    elementsList.add(value.get(0));
                } else {
                    break;
                }
            }
        }
        resultMap.put(maxSize, elementsList);
        return resultMap;
    }

    private static String getCssQuery(String tagName, Attribute attribute) {
        String cssQuery2 = tagName + "[" + attribute.getKey() + "=\"" + attribute.getValue() + "\"]";
        return cssQuery2;
    }

    private static String getPath(Element element) {
        StringBuilder path = new StringBuilder();
        Element currentElement = element;
        while (!currentElement.tagName().equals(ROOT_ELEMENT_VALUE)) {
            long index = 0;
            final int elementSiblingIndex = currentElement.siblingIndex();
            final String elementTagName = currentElement.tagName();
            if (currentElement.parent() != null) {
                index = currentElement.parent().children().stream().
                        filter(el -> el.tagName().equals(elementTagName)
                                && el.siblingIndex() < elementSiblingIndex).count();
            }
            String elementIndex = index != 0 ? "[" + index + "]" : "";
            if (path.length() == 0) {
                path = new StringBuilder(elementTagName + elementIndex);
            } else {
                path.insert(0, elementTagName + elementIndex + TAG_SPLITTER);
            }
            currentElement = currentElement.parent();
        }
        return path.toString();
    }


}
