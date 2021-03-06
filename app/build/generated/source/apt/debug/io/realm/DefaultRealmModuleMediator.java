package io.realm;


import android.util.JsonReader;
import io.realm.internal.ColumnInfo;
import io.realm.internal.OsObjectSchemaInfo;
import io.realm.internal.OsSchemaInfo;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.Row;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

@io.realm.annotations.RealmModule
class DefaultRealmModuleMediator extends RealmProxyMediator {

    private static final Set<Class<? extends RealmModel>> MODEL_CLASSES;
    static {
        Set<Class<? extends RealmModel>> modelClasses = new HashSet<Class<? extends RealmModel>>(4);
        modelClasses.add(com.vocabulary.realm.LearnOverview.class);
        modelClasses.add(com.vocabulary.realm.Test.class);
        modelClasses.add(com.vocabulary.realm.Phrase.class);
        modelClasses.add(com.vocabulary.realm.Vocabulary.class);
        MODEL_CLASSES = Collections.unmodifiableSet(modelClasses);
    }

    @Override
    public Map<Class<? extends RealmModel>, OsObjectSchemaInfo> getExpectedObjectSchemaInfoMap() {
        Map<Class<? extends RealmModel>, OsObjectSchemaInfo> infoMap = new HashMap<Class<? extends RealmModel>, OsObjectSchemaInfo>(4);
        infoMap.put(com.vocabulary.realm.LearnOverview.class, io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.vocabulary.realm.Test.class, io.realm.com_vocabulary_realm_TestRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.vocabulary.realm.Phrase.class, io.realm.com_vocabulary_realm_PhraseRealmProxy.getExpectedObjectSchemaInfo());
        infoMap.put(com.vocabulary.realm.Vocabulary.class, io.realm.com_vocabulary_realm_VocabularyRealmProxy.getExpectedObjectSchemaInfo());
        return infoMap;
    }

    @Override
    public ColumnInfo createColumnInfo(Class<? extends RealmModel> clazz, OsSchemaInfo schemaInfo) {
        checkClass(clazz);

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return io.realm.com_vocabulary_realm_TestRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return io.realm.com_vocabulary_realm_PhraseRealmProxy.createColumnInfo(schemaInfo);
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return io.realm.com_vocabulary_realm_VocabularyRealmProxy.createColumnInfo(schemaInfo);
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public String getSimpleClassNameImpl(Class<? extends RealmModel> clazz) {
        checkClass(clazz);

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return "LearnOverview";
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return "Test";
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return "Phrase";
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return "Vocabulary";
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E newInstance(Class<E> clazz, Object baseRealm, Row row, ColumnInfo columnInfo, boolean acceptDefaultValue, List<String> excludeFields) {
        final BaseRealm.RealmObjectContext objectContext = BaseRealm.objectContext.get();
        try {
            objectContext.set((BaseRealm) baseRealm, row, columnInfo, acceptDefaultValue, excludeFields);
            checkClass(clazz);

            if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
                return clazz.cast(new io.realm.com_vocabulary_realm_LearnOverviewRealmProxy());
            }
            if (clazz.equals(com.vocabulary.realm.Test.class)) {
                return clazz.cast(new io.realm.com_vocabulary_realm_TestRealmProxy());
            }
            if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
                return clazz.cast(new io.realm.com_vocabulary_realm_PhraseRealmProxy());
            }
            if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
                return clazz.cast(new io.realm.com_vocabulary_realm_VocabularyRealmProxy());
            }
            throw getMissingProxyClassException(clazz);
        } finally {
            objectContext.clear();
        }
    }

    @Override
    public Set<Class<? extends RealmModel>> getModelClasses() {
        return MODEL_CLASSES;
    }

    @Override
    public <E extends RealmModel> E copyOrUpdate(Realm realm, E obj, boolean update, Map<RealmModel, RealmObjectProxy> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<E> clazz = (Class<E>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass());

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.copyOrUpdate(realm, (com.vocabulary.realm.LearnOverview) obj, update, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_TestRealmProxy.copyOrUpdate(realm, (com.vocabulary.realm.Test) obj, update, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_PhraseRealmProxy.copyOrUpdate(realm, (com.vocabulary.realm.Phrase) obj, update, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_VocabularyRealmProxy.copyOrUpdate(realm, (com.vocabulary.realm.Vocabulary) obj, update, cache));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public void insert(Realm realm, RealmModel object, Map<RealmModel, Long> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insert(realm, (com.vocabulary.realm.LearnOverview) object, cache);
        } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
            io.realm.com_vocabulary_realm_TestRealmProxy.insert(realm, (com.vocabulary.realm.Test) object, cache);
        } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            io.realm.com_vocabulary_realm_PhraseRealmProxy.insert(realm, (com.vocabulary.realm.Phrase) object, cache);
        } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            io.realm.com_vocabulary_realm_VocabularyRealmProxy.insert(realm, (com.vocabulary.realm.Vocabulary) object, cache);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public void insert(Realm realm, Collection<? extends RealmModel> objects) {
        Iterator<? extends RealmModel> iterator = objects.iterator();
        RealmModel object = null;
        Map<RealmModel, Long> cache = new HashMap<RealmModel, Long>(objects.size());
        if (iterator.hasNext()) {
            //  access the first element to figure out the clazz for the routing below
            object = iterator.next();
            // This cast is correct because obj is either
            // generated by RealmProxy or the original type extending directly from RealmObject
            @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

            if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
                io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insert(realm, (com.vocabulary.realm.LearnOverview) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
                io.realm.com_vocabulary_realm_TestRealmProxy.insert(realm, (com.vocabulary.realm.Test) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
                io.realm.com_vocabulary_realm_PhraseRealmProxy.insert(realm, (com.vocabulary.realm.Phrase) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
                io.realm.com_vocabulary_realm_VocabularyRealmProxy.insert(realm, (com.vocabulary.realm.Vocabulary) object, cache);
            } else {
                throw getMissingProxyClassException(clazz);
            }
            if (iterator.hasNext()) {
                if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
                    io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
                    io.realm.com_vocabulary_realm_TestRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
                    io.realm.com_vocabulary_realm_PhraseRealmProxy.insert(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
                    io.realm.com_vocabulary_realm_VocabularyRealmProxy.insert(realm, iterator, cache);
                } else {
                    throw getMissingProxyClassException(clazz);
                }
            }
        }
    }

    @Override
    public void insertOrUpdate(Realm realm, RealmModel obj, Map<RealmModel, Long> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass());

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.LearnOverview) obj, cache);
        } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
            io.realm.com_vocabulary_realm_TestRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Test) obj, cache);
        } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            io.realm.com_vocabulary_realm_PhraseRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Phrase) obj, cache);
        } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            io.realm.com_vocabulary_realm_VocabularyRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Vocabulary) obj, cache);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public void insertOrUpdate(Realm realm, Collection<? extends RealmModel> objects) {
        Iterator<? extends RealmModel> iterator = objects.iterator();
        RealmModel object = null;
        Map<RealmModel, Long> cache = new HashMap<RealmModel, Long>(objects.size());
        if (iterator.hasNext()) {
            //  access the first element to figure out the clazz for the routing below
            object = iterator.next();
            // This cast is correct because obj is either
            // generated by RealmProxy or the original type extending directly from RealmObject
            @SuppressWarnings("unchecked") Class<RealmModel> clazz = (Class<RealmModel>) ((object instanceof RealmObjectProxy) ? object.getClass().getSuperclass() : object.getClass());

            if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
                io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.LearnOverview) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
                io.realm.com_vocabulary_realm_TestRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Test) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
                io.realm.com_vocabulary_realm_PhraseRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Phrase) object, cache);
            } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
                io.realm.com_vocabulary_realm_VocabularyRealmProxy.insertOrUpdate(realm, (com.vocabulary.realm.Vocabulary) object, cache);
            } else {
                throw getMissingProxyClassException(clazz);
            }
            if (iterator.hasNext()) {
                if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
                    io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Test.class)) {
                    io.realm.com_vocabulary_realm_TestRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
                    io.realm.com_vocabulary_realm_PhraseRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
                    io.realm.com_vocabulary_realm_VocabularyRealmProxy.insertOrUpdate(realm, iterator, cache);
                } else {
                    throw getMissingProxyClassException(clazz);
                }
            }
        }
    }

    @Override
    public <E extends RealmModel> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update)
        throws JSONException {
        checkClass(clazz);

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_TestRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_PhraseRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_VocabularyRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader)
        throws IOException {
        checkClass(clazz);

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_TestRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_PhraseRealmProxy.createUsingJsonStream(realm, reader));
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_VocabularyRealmProxy.createUsingJsonStream(realm, reader));
        }
        throw getMissingProxyClassException(clazz);
    }

    @Override
    public <E extends RealmModel> E createDetachedCopy(E realmObject, int maxDepth, Map<RealmModel, RealmObjectProxy.CacheData<RealmModel>> cache) {
        // This cast is correct because obj is either
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<E> clazz = (Class<E>) realmObject.getClass().getSuperclass();

        if (clazz.equals(com.vocabulary.realm.LearnOverview.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_LearnOverviewRealmProxy.createDetachedCopy((com.vocabulary.realm.LearnOverview) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Test.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_TestRealmProxy.createDetachedCopy((com.vocabulary.realm.Test) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Phrase.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_PhraseRealmProxy.createDetachedCopy((com.vocabulary.realm.Phrase) realmObject, 0, maxDepth, cache));
        }
        if (clazz.equals(com.vocabulary.realm.Vocabulary.class)) {
            return clazz.cast(io.realm.com_vocabulary_realm_VocabularyRealmProxy.createDetachedCopy((com.vocabulary.realm.Vocabulary) realmObject, 0, maxDepth, cache));
        }
        throw getMissingProxyClassException(clazz);
    }

}
