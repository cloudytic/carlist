package controllers;

import models.*;
import pojos.Param;
import services.DB;
import utilities.DateUtil;

import java.util.*;

public class USelect {

    public static TreeSet<AutoMake> MAKES;
    public static TreeSet<AutoModel> MODELS;
    public static TreeSet<AutoBodyType> BODY_TYPES;
    public static TreeSet<AddressState> STATES;
    public static TreeSet<AddressLocality> LOCALITIES;
    public static TreeSet<AutoCondition> CONDITIONS;

    public static TreeSet<AutoMake> listMakes() {
        if(MAKES == null) {
            MAKES = new TreeSet<>(DB.find(AutoMake.class, DB.where().field("hide", false)));
        }
        return MAKES;
    }

    public static TreeSet<AutoModel> listModels() {
        if(MODELS == null) {
            MODELS = new TreeSet<>(DB.find(AutoModel.class, DB.where().field("hide", false)));
        }
        return MODELS;
    }

    public static TreeSet<AutoBodyType> listBodyTypes() {
        if(BODY_TYPES == null) {
            BODY_TYPES = new TreeSet<>(DB.find(AutoBodyType.class, DB.where().field("hide", false)));
        }
        return BODY_TYPES;
    }

    public static TreeSet<AddressState> listStates() {
        if(STATES == null) {
            STATES = new TreeSet<>(DB.find(AddressState.class, DB.where().field("hide", false), Param.getAll("id", "asc")));
        }
        return STATES;
    }

    public static TreeSet<AddressLocality> listLocalities() {
        if(LOCALITIES == null) {
            LOCALITIES = new TreeSet<>(DB.find(AddressLocality.class, DB.where().field("hide", false)));
        }
        return LOCALITIES;
    }

    public static TreeSet<AutoCondition> listConditions() {
        if(CONDITIONS == null) {
            CONDITIONS = new TreeSet<>(DB.find(AutoCondition.class, DB.where().field("hide", false)));
        }
        return CONDITIONS;
    }

    public static TreeSet<AutoCategory> listCategories() {
        return new TreeSet<>(DB.find(AutoCategory.class, DB.where().field("hide", false)));
    }

    public static TreeSet<AutoTransmission> listTransmissions() {
        return new TreeSet<>(DB.find(AutoTransmission.class, DB.where().field("hide", false)));
    }

    public static TreeSet<AutoFuelType> listFuelTypes() {
        return new TreeSet<>(DB.find(AutoFuelType.class, DB.where().field("hide", false)));
    }

    public static TreeSet<AutoColor> listColors() {
        return new TreeSet<>(DB.find(AutoColor.class, DB.where().field("hide", false)));
    }

    public static TreeSet<AutoDriveTrain> listDrives() {
        return new TreeSet<>(DB.find(AutoDriveTrain.class, DB.where().field("hide", false)));
    }

    public static TreeSet<AutoFeature> listFeatures() {
        return new TreeSet<>(DB.find(AutoFeature.class, DB.where().field("hide", false)));
    }

    public static Map<String, String> selectStates(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AddressState state: listStates()) {
            if(opt == 1) {
                map.put(state.getId().toString(), state.getName());
            } else if(opt == 2) {
                map.put(state.getSlug(), state.getName());
            }
        }
        return map;
    }


    public static Map<String, String> selectMakes(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoMake make: listMakes()) {
            if(opt == 1) {
                map.put(make.getId().toString(), make.getName());
            } else if(opt == 2) {
                map.put(make.getSlug(), make.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectModels(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for (AutoModel model : listModels()) {
            if(opt == 1) {
                map.put(model.getId().toString(), model.getName());
            } else if(opt == 2) {
                map.put(model.getSlug(), model.getName());
            }
        }
        return map;
    }

    public static Map<String, Map<String, String>> getMakeModels(int opt) {
        Map<String, Map<String, String>> makeModels = new LinkedHashMap<>();
        for (AutoMake make : listMakes()) {
            Map<String, String> models = new LinkedHashMap<>();
            listModels().forEach(model -> {
                if(model.getParent() != null && make.getId().equals(model.getParent().getId())) {
                    if(opt == 1) {
                        models.put(model.getId().toString(), model.getName());
                    } else if(opt == 2) {
                        models.put(model.getSlug(), model.getName());
                    }
                }
            });
            makeModels.put(make.getName(), models);
        }
        return makeModels;
    }

    public static Map<String, Map<String, String>> getStateLocalities(int opt) {
        Map<String, Map<String, String>> stateLocalities = new LinkedHashMap<>();
        for (AddressState state : listStates()) {
            Map<String, String> localities = new LinkedHashMap<>();
            listLocalities().forEach(locality -> {
                if(locality.getParent() != null && state.getId().equals(locality.getParent().getId())) {
                    if(opt == 1) {
                        localities.put(locality.getId().toString(), locality.getName());
                    } else if(opt == 2) {
                        localities.put(locality.getSlug(), locality.getName());
                    }
                }
            });
            stateLocalities.put(state.getName(), localities);
        }
        return stateLocalities;
    }

    public static Map<String, String> selectConditions(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoCondition condition: DB.find(AutoCondition.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(condition.getId().toString(), condition.getName());
            } else if(opt == 2) {
                map.put(condition.getSlug(), condition.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectCategories(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoCategory category: DB.find(AutoCategory.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(category.getId().toString(), category.getName());
            } else if(opt == 2) {
                map.put(category.getSlug(), category.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectTransmissions(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoTransmission transmission: DB.find(AutoTransmission.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(transmission.getId().toString(), transmission.getName());
            } else if(opt == 2) {
                map.put(transmission.getSlug(), transmission.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectFuelTypes(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoFuelType fuelType: DB.find(AutoFuelType.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(fuelType.getId().toString(), fuelType.getName());
            } else if(opt == 2) {
                map.put(fuelType.getSlug(), fuelType.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectBodyTypes(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoBodyType bodyType: DB.find(AutoBodyType.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(bodyType.getId().toString(), bodyType.getName());
            } else if(opt == 2) {
                map.put(bodyType.getSlug(), bodyType.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectColors(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoColor color: DB.find(AutoColor.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(color.getId().toString(), color.getName());
            } else if(opt == 2) {
                map.put(color.getSlug(), color.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectFeatures(int opt) {
        Map<String, String> selectFeatures = new LinkedHashMap<>();
        Param param = Param.getAll("name", "asc");
        List<AutoFeature> list = DB.find(AutoFeature.class, DB.where().field("hide", false), param);
        for(AutoFeature feature : list) {
            if(opt == 1) {
                selectFeatures.put(feature.id.toString(), feature.name);
            } else if(opt == 2) {
                selectFeatures.put(feature.slug, feature.name);
            }
        }
        return selectFeatures;
    }

    public static Map<String, String> selectDriveTrains(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(AutoDriveTrain drive: DB.find(AutoDriveTrain.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(drive.getId().toString(), drive.getName());
            } else if(opt == 2) {
                map.put(drive.getSlug(), drive.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectNums() {
        Map<String, String> map = new LinkedHashMap<>();
        for(int i = 1; i<11; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
        return map;
    }

    public static Map<String, String> selectYears() {
        Map<String, String> map = new LinkedHashMap<>();
        for(int i = DateUtil.getCurrentYear(); i >= 1970 ; i--) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
        return map;
    }

    public static Map<String, String> selectBlogCategories(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(BlogCategory category: DB.find(BlogCategory.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(category.getId().toString(), category.getName());
            } else if(opt == 2) {
                map.put(category.getSlug(), category.getName());
            }
        }
        return map;
    }

    public static Map<String, String> selectBlogTags(int opt) {
        Map<String, String> map = new LinkedHashMap<>();
        for(BlogTag tag: DB.find(BlogTag.class, DB.where().field("hide", false))) {
            if(opt == 1) {
                map.put(tag.getId().toString(), tag.getName());
            } else if(opt == 2) {
                map.put(tag.getSlug(), tag.getName());
            }
        }
        return map;
    }

    public static void reset() {
        MAKES = null;
        MODELS = null;
        BODY_TYPES = null;
    }

    public static List<String> searchIndexLocalities;
    public static List<String> indexLocalities() {
        if(searchIndexLocalities == null) {
            searchIndexLocalities = new ArrayList<>();
            TreeSet<AddressLocality> list = listLocalities();
            for(AddressLocality locality: list) {
                searchIndexLocalities.add(locality.slug);
            }
        }
        return searchIndexLocalities;
    }

    public static List<String> searchIndexStates;
    public static List<String> indexStates() {
        if(searchIndexStates == null) {
            searchIndexStates = new ArrayList<>();
            TreeSet<AddressState> list = listStates();
            for(AddressState state : list) {
                searchIndexStates.add(state.slug);
            }
        }
        return searchIndexStates;
    }


    public static List<String> searchIndexMakes;
    public static List<String> indexMakes() {
        if(searchIndexMakes == null) {
            searchIndexMakes = new ArrayList<>();
            TreeSet<AutoMake> list = listMakes();
            for(AutoMake make : list) {
                searchIndexMakes.add(make.slug);
            }
        }
        return searchIndexMakes;
    }

    public static List<String> searchIndexModels;
    public static List<String> indexModels() {
        if(searchIndexModels == null) {
            searchIndexModels = new ArrayList<>();
            TreeSet<AutoModel> list = listModels();
            for(AutoModel model : list) {
                searchIndexModels.add(model.slug);
            }
        }
        return searchIndexModels;
    }


    public static List<String> searchIndexBodyTypes;
    public static List<String> indexBodyTypes() {
        if(searchIndexBodyTypes == null) {
            searchIndexBodyTypes = new ArrayList<>();
            TreeSet<AutoBodyType> list = listBodyTypes();
            for(AutoBodyType bodyType : list) {
                searchIndexBodyTypes.add(bodyType.slug);
            }
        }
        return searchIndexBodyTypes;
    }
}
