package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import models.AddressState;
import models.AutoMake;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.*;

public class ApiFieldsController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public ApiFieldsController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result fields() {

        Map<String, Object> map = new LinkedHashMap<>();
        Map<Long, Entry> makeModels = getMakeModels();
        Map<Long, Entry> stateLocalities = getStateLocalities();
        map.put("makeModels", makeModels);
        map.put("stateLocalities", stateLocalities);

        map.put("categories", getCategories());
        map.put("conditions", getConditions());
        map.put("transmissions", getTransmissions());
        map.put("fuelTypes", getFuelTypes());
        map.put("bodyTypes", getBodyTypes());
        map.put("colors", getColors());
        map.put("drives", getDrives());
        map.put("features", getFeatures());

        JsonNode json = Json.toJson(map);
        return ok(json);
    }

    public Map<Long, Element> getCategories() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listCategories().forEach(category -> {
            Element element = new Element(category.getId(), category.getName());
            map.put(category.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getConditions() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listConditions().forEach(condition -> {
            Element element = new Element(condition.getId(), condition.getName());
            map.put(condition.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getTransmissions() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listTransmissions().forEach(transmission -> {
            Element element = new Element(transmission.getId(), transmission.getName());
            map.put(transmission.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getFuelTypes() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listFuelTypes().forEach(fuelType -> {
            Element element = new Element(fuelType.getId(), fuelType.getName());
            map.put(fuelType.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getBodyTypes() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listBodyTypes().forEach(bodyType -> {
            Element element = new Element(bodyType.getId(), bodyType.getName());
            map.put(bodyType.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getColors() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listColors().forEach(color -> {
            Element element = new Element(color.getId(), color.getName());
            map.put(color.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getDrives() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listDrives().forEach(driveTrain -> {
            Element element = new Element(driveTrain.getId(), driveTrain.getName());
            map.put(driveTrain.getId(), element);
        });
        return map;
    }

    public Map<Long, Element> getFeatures() {
        Map<Long, Element> map = new LinkedHashMap<>();
        USelect.listFeatures().forEach(feature -> {
            Element element = new Element(feature.getId(), feature.getName());
            map.put(feature.getId(), element);
        });
        return map;
    }

    public Result makeModels() {
        Map<Long, Entry> makeModels = getMakeModels();
        JsonNode json = Json.toJson(makeModels);
        return ok(json);
    }

    public Result stateLocalities() {
        Map<Long, Entry> stateLocalities = getStateLocalities();
        JsonNode json = Json.toJson(stateLocalities);
        return ok(json);
    }

    private static Map<Long, Entry> getMakeModels() {
        Map<Long, Entry> makeModels = new LinkedHashMap<>();
        for (AutoMake make: USelect.listMakes()) {
            List<Element> modelList = new LinkedList<>();
            USelect.listModels().forEach(model -> {
                if(model.getParent() != null && make.getId().equals(model.getParent().getId())) {
                    Element modelELement = new Element(model.getId(), model.getName());
                    modelList.add(modelELement);
                }
            });
            Entry entry = new Entry(make.getId(), make.getName(), modelList);
            makeModels.put(make.getId(), entry);
        }
        return makeModels;
    }

    private static Map<Long, Entry> getStateLocalities() {
        Map<Long, Entry>  stateLocalities = new LinkedHashMap<>();
        for (AddressState state: USelect.listStates()) {
            List<Element> localityList = new LinkedList<>();
            USelect.listLocalities().forEach(locality -> {
                if(locality.getParent() != null && state.getId().equals(locality.getParent().getId())) {
                    Element localityElement = new Element(locality.getId(), locality.getName());
                    localityList.add(localityElement);
                }
            });
            Entry entry = new Entry(state.getId(), state.getName(), localityList);
            stateLocalities.put(state.getId(),  entry);
        }
        return stateLocalities;
    }

    @Setter @Getter
    public static class Entry {
        private Long id;
        private String name;
        List<Element> children;
        public Entry(Long id, String name, List<Element> children) {
            this.id = id;
            this.name = name;
            this.children = children;
        }
    }

    public static class Element {
        @Setter @Getter
        private Long id;
        @Setter @Getter
        private String name;
        private final int hashCode;
        public Element(Long id, String name) {
            this.id = id;
            this.name = name;
            this.hashCode = Objects.hash(id, name);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Element that = (Element) o;
            return Objects.equals(id, that.id) && name.equals(that.name);
        }
        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
