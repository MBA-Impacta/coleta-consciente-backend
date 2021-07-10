CREATE TABLE IF NOT EXISTS tb_location
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    registration_date timestamp(0) without time zone NOT NULL,
    update_date timestamp(0) without time zone NOT NULL,
    latitude numeric NOT NULL,
    longitude numeric NOT NULL,
    street character varying(255) COLLATE pg_catalog."default" NOT NULL,
    number character varying(8) COLLATE pg_catalog."default" NOT NULL,
    complement character varying(15) COLLATE pg_catalog."default",
    material varchar(10) NOT NULL,
    CONSTRAINT location_pkey PRIMARY KEY (id)
);