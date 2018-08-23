package com.vocabulary.realm;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        /** Version: default
         *  Class Phrase
         *       @PrimaryKey
         *       private String date;
         *       @Required
         *       @Index
         *       private String p1;
         *       @Required
         *       private String p2;
         *       @Required
         *       private String language;
         *       @Required
         *       private String vocabularyId;
         *
         *  Version: 2
         *  Class Phrase
         *       @PrimaryKey
         *       private String date;
         *       @Required
         *       @Index
         *       private String p1;
         *       @Required
         *       @Index
         *       private String p2;
         *       @Required
         *       private String language;
         *       @Required
         *       private String vocabularyId;
         *       @Required
         *       private Integer mKnowCount;
         *       @Required
         *       private Integer mDontKnowCount;
         **/
        if (oldVersion < 2) {
            RealmObjectSchema phraseSchema = schema.get("Phrase");

            phraseSchema
                    .addIndex(Phrase.P2)
                    .addField(Phrase.KNOW_COUNT, int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set(Phrase.KNOW_COUNT, 0);
                        }
                    })
                    .addField(Phrase.DONT_KNOW_COUNT, int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set(Phrase.DONT_KNOW_COUNT, 0);
                        }
                    });

            oldVersion++;
        }
    }
}
