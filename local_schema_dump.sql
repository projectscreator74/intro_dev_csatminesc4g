--
-- PostgreSQL database dump
--

\restrict VvekIaaxWwEycmnqerw5U5L9OMDrFZjByt1yKLOo32crwL18sXaFj9Pe6eIJFAr

-- Dumped from database version 16.14 (Debian 16.14-1.pgdg13+1)
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account (
    user_id integer NOT NULL,
    user_name character varying(50),
    password character varying(255) NOT NULL,
    email_id character varying(255) NOT NULL,
    auth_token character varying(255)
);


--
-- Name: account_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.account_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: account_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.account_user_id_seq OWNED BY public.account.user_id;


--
-- Name: assignment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.assignment (
    assignment_id integer NOT NULL,
    user_id integer,
    timedate timestamp without time zone,
    grade character varying(10),
    user_score numeric(6,2),
    total_points numeric(6,2),
    notes text,
    ext_links character varying(255),
    status_id integer,
    title character varying(255),
    class_id integer,
    ext_id character varying(255),
    completed boolean DEFAULT false,
    due_label character varying(50)
);


--
-- Name: assignment_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.assignment_assignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: assignment_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.assignment_assignment_id_seq OWNED BY public.assignment.assignment_id;


--
-- Name: class; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.class (
    class_id integer NOT NULL,
    user_id integer,
    class_name character varying(100) NOT NULL,
    period character varying(20),
    overall_grade numeric(5,2),
    ext_id character varying(255)
);


--
-- Name: class_class_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.class_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: class_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.class_class_id_seq OWNED BY public.class.class_id;


--
-- Name: event; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.event (
    event_id integer NOT NULL,
    user_id integer,
    timedate timestamp without time zone,
    notes text,
    ext_links character varying(255),
    location character varying(255),
    estimated_hours numeric(5,2),
    start_time timestamp without time zone,
    end_time_bool boolean DEFAULT false,
    end_time timestamp without time zone,
    ext_id_bool boolean DEFAULT false,
    ext_id character varying(255),
    status_id integer,
    title character varying(255),
    class_id integer
);


--
-- Name: event_event_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.event_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.event_event_id_seq OWNED BY public.event.event_id;


--
-- Name: exam; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.exam (
    exam_id integer NOT NULL,
    user_id integer,
    timedate timestamp without time zone,
    grade character varying(10),
    user_score numeric(6,2),
    total_points numeric(6,2),
    notes text,
    ext_links character varying(255),
    status_id integer,
    title character varying(255),
    class_id integer,
    ext_id character varying(255)
);


--
-- Name: exam_exam_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.exam_exam_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: exam_exam_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.exam_exam_id_seq OWNED BY public.exam.exam_id;


--
-- Name: file; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.file (
    file_id integer NOT NULL,
    ext_links character varying(255),
    file_size numeric(10,2),
    user_id integer,
    file_name character varying(255),
    file_type character varying(50),
    category character varying(50)
);


--
-- Name: file_file_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.file_file_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: file_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.file_file_id_seq OWNED BY public.file.file_id;


--
-- Name: goals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.goals (
    goal_id integer NOT NULL,
    user_id integer,
    timedate timestamp without time zone,
    notes text,
    ext_links character varying(255),
    status_id integer,
    title character varying(255)
);


--
-- Name: goals_goal_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.goals_goal_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: goals_goal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.goals_goal_id_seq OWNED BY public.goals.goal_id;


--
-- Name: integration_credentials; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.integration_credentials (
    integration_id integer NOT NULL,
    user_id integer,
    provider character varying(20) NOT NULL,
    field_1 character varying(255),
    field_2 character varying(255),
    connected_at timestamp without time zone DEFAULT now(),
    field_3 character varying(255)
);


--
-- Name: integration_credentials_integration_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.integration_credentials_integration_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: integration_credentials_integration_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.integration_credentials_integration_id_seq OWNED BY public.integration_credentials.integration_id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    notification_id integer NOT NULL,
    user_id integer,
    timedate timestamp without time zone,
    notes text,
    ext_links character varying(255),
    notify_type_id integer,
    notify_sound_bool boolean DEFAULT false,
    notify_sound character varying(255),
    title character varying(255),
    notif_color character varying(20),
    is_read boolean DEFAULT false
);


--
-- Name: notifications_notification_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notifications_notification_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notifications_notification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notifications_notification_id_seq OWNED BY public.notifications.notification_id;


--
-- Name: notify_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notify_type (
    notify_type_id integer NOT NULL,
    event_alert boolean DEFAULT false,
    assignment_alert boolean DEFAULT false,
    exam_alert boolean DEFAULT false,
    goal_alert boolean DEFAULT false,
    schedule_alert_conflict boolean DEFAULT false
);


--
-- Name: notify_type_notify_type_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notify_type_notify_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notify_type_notify_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notify_type_notify_type_id_seq OWNED BY public.notify_type.notify_type_id;


--
-- Name: profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.profile (
    user_id integer NOT NULL,
    display_name character varying(100),
    profile_picture character varying(255),
    notify_enabled boolean DEFAULT true,
    grade_benchmark numeric(5,2)
);


--
-- Name: status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.status (
    status_id integer NOT NULL,
    status_name character varying(50) NOT NULL
);


--
-- Name: status_status_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.status_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: status_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.status_status_id_seq OWNED BY public.status.status_id;


--
-- Name: account user_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account ALTER COLUMN user_id SET DEFAULT nextval('public.account_user_id_seq'::regclass);


--
-- Name: assignment assignment_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assignment ALTER COLUMN assignment_id SET DEFAULT nextval('public.assignment_assignment_id_seq'::regclass);


--
-- Name: class class_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class ALTER COLUMN class_id SET DEFAULT nextval('public.class_class_id_seq'::regclass);


--
-- Name: event event_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event ALTER COLUMN event_id SET DEFAULT nextval('public.event_event_id_seq'::regclass);


--
-- Name: exam exam_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam ALTER COLUMN exam_id SET DEFAULT nextval('public.exam_exam_id_seq'::regclass);


--
-- Name: file file_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.file ALTER COLUMN file_id SET DEFAULT nextval('public.file_file_id_seq'::regclass);


--
-- Name: goals goal_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.goals ALTER COLUMN goal_id SET DEFAULT nextval('public.goals_goal_id_seq'::regclass);


--
-- Name: integration_credentials integration_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.integration_credentials ALTER COLUMN integration_id SET DEFAULT nextval('public.integration_credentials_integration_id_seq'::regclass);


--
-- Name: notifications notification_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications ALTER COLUMN notification_id SET DEFAULT nextval('public.notifications_notification_id_seq'::regclass);


--
-- Name: notify_type notify_type_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notify_type ALTER COLUMN notify_type_id SET DEFAULT nextval('public.notify_type_notify_type_id_seq'::regclass);


--
-- Name: status status_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.status ALTER COLUMN status_id SET DEFAULT nextval('public.status_status_id_seq'::regclass);


--
-- Name: account account_email_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_email_id_key UNIQUE (email_id);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (user_id);


--
-- Name: account account_user_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_user_name_key UNIQUE (user_name);


--
-- Name: assignment assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assignment
    ADD CONSTRAINT assignment_pkey PRIMARY KEY (assignment_id);


--
-- Name: class class_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_id);


--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (event_id);


--
-- Name: exam exam_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam
    ADD CONSTRAINT exam_pkey PRIMARY KEY (exam_id);


--
-- Name: file file_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_pkey PRIMARY KEY (file_id);


--
-- Name: goals goals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.goals
    ADD CONSTRAINT goals_pkey PRIMARY KEY (goal_id);


--
-- Name: integration_credentials integration_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.integration_credentials
    ADD CONSTRAINT integration_credentials_pkey PRIMARY KEY (integration_id);


--
-- Name: integration_credentials integration_credentials_user_id_provider_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.integration_credentials
    ADD CONSTRAINT integration_credentials_user_id_provider_key UNIQUE (user_id, provider);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (notification_id);


--
-- Name: notify_type notify_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notify_type
    ADD CONSTRAINT notify_type_pkey PRIMARY KEY (notify_type_id);


--
-- Name: profile profile_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (user_id);


--
-- Name: status status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (status_id);


--
-- Name: status status_status_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_status_name_key UNIQUE (status_name);


--
-- Name: assignment assignment_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assignment
    ADD CONSTRAINT assignment_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: assignment assignment_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assignment
    ADD CONSTRAINT assignment_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(status_id);


--
-- Name: assignment assignment_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assignment
    ADD CONSTRAINT assignment_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: class class_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: event event_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: event event_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(status_id);


--
-- Name: event event_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: exam exam_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam
    ADD CONSTRAINT exam_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: exam exam_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam
    ADD CONSTRAINT exam_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(status_id);


--
-- Name: exam exam_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam
    ADD CONSTRAINT exam_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: file file_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: goals goals_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.goals
    ADD CONSTRAINT goals_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(status_id);


--
-- Name: goals goals_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.goals
    ADD CONSTRAINT goals_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: integration_credentials integration_credentials_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.integration_credentials
    ADD CONSTRAINT integration_credentials_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: notifications notifications_notify_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_notify_type_id_fkey FOREIGN KEY (notify_type_id) REFERENCES public.notify_type(notify_type_id);


--
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- Name: profile profile_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.account(user_id);


--
-- PostgreSQL database dump complete
--

\unrestrict VvekIaaxWwEycmnqerw5U5L9OMDrFZjByt1yKLOo32crwL18sXaFj9Pe6eIJFAr

