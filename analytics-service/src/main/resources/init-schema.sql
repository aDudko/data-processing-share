DROP SCHEMA IF EXISTS analytics CASCADE;

CREATE SCHEMA analytics;

CREATE TABLE analytics.source_analytics (
    id uuid NOT NULL,
    word character varying COLLATE pg_catalog."default" NOT NULL,
    word_count bigint NOT NULL,
    record_date time with time zone,
    CONSTRAINT source_analytics_pkey PRIMARY KEY (id)
);

ALTER TABLE analytics.source_analytics
    OWNER to postgres;

CREATE INDEX "INDX_WORDBY_DATE"
    ON analytics.source_analytics USING btree
        (word COLLATE pg_catalog."default" ASC NULLS LAST, record_date DESC NULLS LAST)
    TABLESPACE pg_default;