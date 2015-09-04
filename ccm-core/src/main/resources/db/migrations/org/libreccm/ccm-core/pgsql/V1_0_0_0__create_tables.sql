
    create table ccm_core.application_types (
        resource_type_id int8 not null,
        container_group_id int8,
        provider_app_type_id int8,
        primary key (resource_type_id)
    );

    create table ccm_core.applications (
        primary_url varchar(1024) not null,
        object_id int8 not null,
        container_group_id int8,
        primary key (object_id)
    );

    create table ccm_core.attachments (
        attachment_id int8 not null,
        attachment_data oid,
        description varchar(255),
        mime_type varchar(255),
        title varchar(255),
        primary key (attachment_id)
    );

    create table ccm_core.categories (
        abstract_category boolean,
        category_order int8,
        enabled boolean,
        name varchar(255) not null,
        unique_id varchar(255) not null,
        visible boolean,
        object_id int8 not null,
        parent_category_id int8,
        primary key (object_id)
    );

    create table ccm_core.categorizations (
        categorization_id int8 not null,
        category_order int8,
        category_index boolean,
        object_order int8,
        object_id int8,
        category_id int8,
        primary key (categorization_id)
    );

    create table ccm_core.category_descriptions (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.category_domains (
        domain_key varchar(255) not null,
        released timestamp,
        uri varchar(1024) not null,
        version varchar(255) not null,
        object_id int8 not null,
        root_category_id int8,
        primary key (object_id)
    );

    create table ccm_core.category_titles (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.ccm_groups (
        name varchar(512) not null,
        subject_id int8 not null,
        primary key (subject_id)
    );

    create table ccm_core.ccm_objects (
        object_id int8 not null,
        display_name varchar(255),
        primary key (object_id)
    );

    create table ccm_core.ccm_privileges (
        privilege_id int8 not null,
        label varchar(255) not null,
        relevant_privilege_id int8,
        primary key (privilege_id)
    );

    create table ccm_core.ccm_revisions (
        id int4 not null,
        timestamp int8 not null,
        user_name varchar(255),
        primary key (id)
    );

    create table ccm_core.ccm_roles (
        role_id int8 not null,
        description varchar(255),
        name varchar(512),
        implicit_group_id int8,
        source_group_id int8,
        primary key (role_id)
    );

    create table ccm_core.ccm_users (
        banned boolean,
        hash_algorithm varchar(64),
        family_name varchar(512),
        given_name varchar(512),
        middle_name varchar(512),
        title_post varchar(512),
        title_pre varchar(512),
        password varchar(2048),
        password_answer varchar(2048),
        password_question varchar(2048),
        password_reset_required boolean,
        salt varchar(2048),
        screen_name varchar(255) not null,
        sso_login varchar(512),
        subject_id int8 not null,
        primary key (subject_id)
    );

    create table ccm_core.digests (
        frequency int4,
        header varchar(4096) not null,
        next_run timestamp,
        digest_separator varchar(128) not null,
        signature varchar(4096) not null,
        subject varchar(255) not null,
        object_id int8 not null,
        from_party_id int8,
        primary key (object_id)
    );

    create table ccm_core.domain_descriptions (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.domain_ownerships (
        ownership_id int8 not null,
        context varchar(255),
        domain_order int8,
        owner_order int8,
        domain_object_id int8 not null,
        owner_object_id int8 not null,
        primary key (ownership_id)
    );

    create table ccm_core.domain_titles (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.formbuilder_component_descriptions (
        component_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (component_id, locale)
    );

    create table ccm_core.formbuilder_components (
        active boolean,
        admin_name varchar(255),
        attribute_string varchar(255),
        component_order int8,
        selected boolean,
        object_id int8 not null,
        parentComponent_object_id int8,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_confirm_email_listener (
        body text,
        from_email varchar(255),
        subject varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_confirm_redirect_listeners (
        url varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_data_driven_selects (
        multiple boolean,
        query varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_data_queries (
        query_id varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_data_query_descriptions (
        data_query_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (data_query_id, locale)
    );

    create table ccm_core.formbuilder_data_query_names (
        data_query_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (data_query_id, locale)
    );

    create table ccm_core.formbuilder_formsections (
        formsection_action varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_listeners (
        attribute_string varchar(255),
        class_name varchar(255),
        object_id int8 not null,
        widget_object_id int8,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_metaobjects (
        class_name varchar(255),
        pretty_name varchar(255),
        pretty_plural varchar(255),
        properties_form varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_object_types (
        app_name varchar(255),
        class_name varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_option_labels (
        option_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (option_id, locale)
    );

    create table ccm_core.formbuilder_options (
        parameter_value varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_process_listener_descriptions (
        process_listener_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (process_listener_id, locale)
    );

    create table ccm_core.formbuilder_process_listener_names (
        process_listener_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (process_listener_id, locale)
    );

    create table ccm_core.formbuilder_process_listeners (
        listener_class varchar(255),
        process_listener_order int8,
        object_id int8 not null,
        formSection_object_id int8,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_remote_server_post_listener (
        remoteUrl varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_simple_email_listeners (
        recipient varchar(255),
        subject varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_template_email_listeners (
        body text,
        recipient varchar(255),
        subject varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_widget_labels (
        object_id int8 not null,
        widget_object_id int8,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_widgets (
        default_value varchar(255),
        parameter_model varchar(255),
        parameter_name varchar(255),
        object_id int8 not null,
        label_object_id int8,
        primary key (object_id)
    );

    create table ccm_core.formbuilder_xml_email_listeners (
        recipient varchar(255),
        subject varchar(255),
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.group_memberships (
        membership_id int8 not null,
        group_subject_id int8,
        user_subject_id int8,
        primary key (membership_id)
    );

    create table ccm_core.hosts (
        host_id int8 not null,
        server_name varchar(512),
        server_port int8,
        primary key (host_id)
    );

    create table ccm_core.inits (
        initializer_id int8 not null,
        class_name varchar(255),
        required_by_id int8,
        primary key (initializer_id)
    );

    create table ccm_core.installed_modules (
        module_id int4 not null,
        module_class_name varchar(2048),
        status varchar(255),
        primary key (module_id)
    );

    create table ccm_core.lucene_documents (
        document_id int8 not null,
        content text,
        content_section varchar(512),
        country varchar(8),
        created timestamp,
        dirty int8,
        document_language varchar(8),
        last_modified timestamp,
        summary varchar(4096),
        document_timestamp timestamp,
        title varchar(4096),
        type varchar(255),
        type_specific_info varchar(512),
        created_by_party_id int8,
        last_modified_by int8,
        primary key (document_id)
    );

    create table ccm_core.lucene_indexes (
        index_id int8 not null,
        lucene_index_id int8,
        host_id int8,
        primary key (index_id)
    );

    create table ccm_core.messages (
        body varchar(255),
        body_mime_type varchar(255),
        sent timestamp,
        subject varchar(255),
        object_id int8 not null,
        in_reply_to_id int8,
        sender_id int8,
        primary key (object_id)
    );

    create table ccm_core.notifications (
        expand_group boolean,
        expunge boolean,
        expunge_message boolean,
        fulfill_date timestamp,
        header varchar(4096),
        max_retries int8,
        request_date timestamp,
        signature varchar(4096),
        status varchar(32),
        object_id int8 not null,
        digest_id int8,
        message_id int8,
        receiver_id int8,
        primary key (object_id)
    );

    create table ccm_core.permissions (
        permission_id int8 not null,
        creation_date timestamp,
        creation_ip varchar(255),
        creation_user_id int8,
        granted_privilege_id int8,
        grantee_id int8,
        object_id int8,
        primary key (permission_id)
    );

    create table ccm_core.portals (
        template boolean,
        object_id int8 not null,
        primary key (object_id)
    );

    create table ccm_core.portlets (
        cell_number int8,
        sort_key int8,
        object_id int8 not null,
        portal_id int8,
        primary key (object_id)
    );

    create table ccm_core.queue_items (
        queue_item_id int8 not null,
        header varchar(4096),
        receiver_address varchar(512),
        retry_count int8,
        signature varchar(4096),
        successful_sended boolean,
        message_id int8,
        receiver_id int8,
        primary key (queue_item_id)
    );

    create table ccm_core.resource_descriptions (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.resource_titles (
        object_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (object_id, locale)
    );

    create table ccm_core.resource_type_descriptions (
        resource_type_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (resource_type_id, locale)
    );

    create table ccm_core.resource_types (
        resource_type_id int8 not null,
        singleton boolean,
        title varchar(254) not null,
        embedded_view boolean,
        full_page_view boolean,
        workspace_app boolean,
        primary key (resource_type_id)
    );

    create table ccm_core.resources (
        created timestamp,
        object_id int8 not null,
        parent_object_id int8,
        resourceType_resource_type_id int8,
        primary key (object_id)
    );

    create table ccm_core.subjects (
        subject_id int8 not null,
        primary key (subject_id)
    );

    create table ccm_core.threads (
        object_id int8 not null,
        root_id int8,
        primary key (object_id)
    );

    create table ccm_core.user_email_addresses (
        user_id int8 not null,
        email_address varchar(512) not null,
        bouncing boolean,
        verified boolean
    );

    create table ccm_core.workflow_descriptions (
        workflow_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (workflow_id, locale)
    );

    create table ccm_core.workflow_names (
        workflow_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (workflow_id, locale)
    );

    create table ccm_core.workflow_task_comments (
        task_id int8 not null,
        comment text
    );

    create table ccm_core.workflow_task_dependencies (
        depends_on_task_id int8 not null,
        dependent_task_id int8 not null
    );

    create table ccm_core.workflow_task_labels (
        task_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (task_id, locale)
    );

    create table ccm_core.workflow_tasks (
        task_id int8 not null,
        active boolean,
        task_state varchar(512),
        workflow_id int8,
        primary key (task_id)
    );

    create table ccm_core.workflow_tasks_descriptions (
        task_id int8 not null,
        localized_value text,
        locale varchar(255) not null,
        primary key (task_id, locale)
    );

    create table ccm_core.workflow_user_task_assigned_groups (
        user_task_id int8 not null,
        assigned_group_id int8 not null
    );

    create table ccm_core.workflow_user_task_assigned_users (
        user_task_id int8 not null,
        assigned_user_id int8 not null
    );

    create table ccm_core.workflow_user_tasks (
        task_id int8 not null,
        active boolean,
        task_state varchar(512),
        workflow_id int8,
        due_date timestamp,
        duration_minutes int8,
        locked boolean,
        start_date timestamp,
        locking_user_id int8,
        notification_sender int8,
        primary key (task_id)
    );

    create table ccm_core.workflows (
        workflow_id int8 not null,
        primary key (workflow_id)
    );

    alter table ccm_core.category_domains 
        add constraint UK_mrgij5fr1sglxyab9ryl1vx37  unique (domain_key);

    alter table ccm_core.category_domains 
        add constraint UK_a9hmskgn6yfbw134mvjy9ixak  unique (uri);

    alter table ccm_core.ccm_groups 
        add constraint UK_9142ut4o9kwqmqjgqynl4xvc6  unique (name);

    alter table ccm_core.ccm_privileges 
        add constraint UK_ir9u47mfn3qds0toon7n5hlai  unique (label);

    alter table ccm_core.ccm_users 
        add constraint UK_3oj1rsneufkapevq9f32y4el0  unique (screen_name);

    alter table ccm_core.hosts 
        add constraint UK_2m0m4m0dhx256d04x2cg3194s  unique (server_name, server_port);

    alter table ccm_core.installed_modules 
        add constraint UK_c2ix7lp01ypyb6jf7b1ieptlm  unique (module_class_name);

    alter table ccm_core.workflow_user_task_assigned_groups 
        add constraint UK_g58x45aybw2yjtwnr9b9itg6c  unique (assigned_group_id);

    alter table ccm_core.workflow_user_task_assigned_users 
        add constraint UK_h62r6cqjp2tdnhscfkgwfupwj  unique (assigned_user_id);

    alter table ccm_core.application_types 
        add constraint FK_r9rd4iekfy3m8r1a1gto4t39 
        foreign key (container_group_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.application_types 
        add constraint FK_i44k6al7mr4u1c76iudglds39 
        foreign key (provider_app_type_id) 
        references ccm_core.application_types;

    alter table ccm_core.application_types 
        add constraint FK_41e4vrshljdkymnhb4cbkroa1 
        foreign key (resource_type_id) 
        references ccm_core.resource_types;

    alter table ccm_core.applications 
        add constraint FK_kr3wur06hmironiamv0rn38nu 
        foreign key (container_group_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.applications 
        add constraint FK_18qjyi037fk2lnx6t9fwljmx0 
        foreign key (object_id) 
        references ccm_core.resources;

    alter table ccm_core.attachments 
        add constraint FK_r3hibvgfo1dmawqig8c563xau 
        foreign key (attachment_id) 
        references ccm_core.messages;

    alter table ccm_core.categories 
        add constraint FK_hfr9rd0rv1jv730afoi2n0qb7 
        foreign key (parent_category_id) 
        references ccm_core.categories;

    alter table ccm_core.categories 
        add constraint FK_hct54n9h1moa76f44g6cw3lpc 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.categorizations 
        add constraint FK_2xymec7oxsvoflm4pyw03qxrw 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.categorizations 
        add constraint FK_spxdunplw881gx7ay4rcuueht 
        foreign key (category_id) 
        references ccm_core.categories;

    alter table ccm_core.category_descriptions 
        add constraint FK_gvqskqclt5nsi6x87163ydldr 
        foreign key (object_id) 
        references ccm_core.categories;

    alter table ccm_core.category_domains 
        add constraint FK_kh4n7uqv126lb1upk45giadxu 
        foreign key (root_category_id) 
        references ccm_core.categories;

    alter table ccm_core.category_domains 
        add constraint FK_irk58v7vtdgx0bfh8yarl5pte 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.category_titles 
        add constraint FK_ygak8bqmh94jjtgs6vg945rd 
        foreign key (object_id) 
        references ccm_core.categories;

    alter table ccm_core.ccm_groups 
        add constraint FK_7a2nhf8gj3lns0preesnlok8o 
        foreign key (subject_id) 
        references ccm_core.subjects;

    alter table ccm_core.ccm_privileges 
        add constraint FK_g06a7mpltqti17tvibm2j7ti8 
        foreign key (relevant_privilege_id) 
        references ccm_core.application_types;

    alter table ccm_core.ccm_roles 
        add constraint FK_ice2oswni34d2xx80cf81v2cv 
        foreign key (implicit_group_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.ccm_roles 
        add constraint FK_kbq9nkjwsvvkt6db59v2c1eb2 
        foreign key (source_group_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.ccm_users 
        add constraint FK_i9x5hcjowqc0aygna4wte5447 
        foreign key (subject_id) 
        references ccm_core.subjects;

    alter table ccm_core.digests 
        add constraint FK_riucjho1m4x84l528d4b0xexh 
        foreign key (from_party_id) 
        references ccm_core.subjects;

    alter table ccm_core.digests 
        add constraint FK_jslyikag80b9qhvvg4ui3r6li 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.domain_descriptions 
        add constraint FK_anq6lql9qv1wov2hoq59i9pfs 
        foreign key (object_id) 
        references ccm_core.category_domains;

    alter table ccm_core.domain_ownerships 
        add constraint FK_nvdejc0jxmru3ax7v0su83wi7 
        foreign key (domain_object_id) 
        references ccm_core.category_domains;

    alter table ccm_core.domain_ownerships 
        add constraint FK_jiilo1lcqv8g7b16cviqhnepy 
        foreign key (owner_object_id) 
        references ccm_core.applications;

    alter table ccm_core.domain_titles 
        add constraint FK_p3w39o4hwcppwotw8ndjey6sl 
        foreign key (object_id) 
        references ccm_core.category_domains;

    alter table ccm_core.formbuilder_component_descriptions 
        add constraint FK_miw32na0kj3r3vx0yd9nmacu3 
        foreign key (component_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_components 
        add constraint FK_ompdvc6pul5xbhn5r2aqv7knb 
        foreign key (parentComponent_object_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_components 
        add constraint FK_2fhckbkcdrahmp1pnnm5p12pf 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_confirm_email_listener 
        add constraint FK_t24egwvbo23ak7ga4cnsmn428 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_confirm_redirect_listeners 
        add constraint FK_7xtmk3ij9uj2f6nybhprm5eh0 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_data_driven_selects 
        add constraint FK_g0cfdd0rrt4akmibhdlejpb9u 
        foreign key (object_id) 
        references ccm_core.formbuilder_widgets;

    alter table ccm_core.formbuilder_data_queries 
        add constraint FK_p2awj0f115oxg1re4nr7wgsvj 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_data_query_descriptions 
        add constraint FK_6vi3n0g1gfjrxd3vvlarrn584 
        foreign key (data_query_id) 
        references ccm_core.formbuilder_data_queries;

    alter table ccm_core.formbuilder_data_query_names 
        add constraint FK_tgnk7hsrmtqxnhvfcefe936v9 
        foreign key (data_query_id) 
        references ccm_core.formbuilder_data_queries;

    alter table ccm_core.formbuilder_formsections 
        add constraint FK_endc2bmlb7orkk4l5x3fkmy2l 
        foreign key (object_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_listeners 
        add constraint FK_fidonwyc6s36a51lilys791ot 
        foreign key (widget_object_id) 
        references ccm_core.formbuilder_widgets;

    alter table ccm_core.formbuilder_listeners 
        add constraint FK_c0gkh6b1dsyp0xh1pvnd6tijr 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_metaobjects 
        add constraint FK_fn61u2xdqraclu9j0y2lxqqp8 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_object_types 
        add constraint FK_pvcmankfvwpvg0lqe6wio4rnc 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_option_labels 
        add constraint FK_e8fy2g61cd7qn8ar1t48g7p1m 
        foreign key (option_id) 
        references ccm_core.formbuilder_options;

    alter table ccm_core.formbuilder_options 
        add constraint FK_f7fgwaysg76tnx2xtfjnpt8a3 
        foreign key (object_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_process_listener_descriptions 
        add constraint FK_p1e4ygtc3ke9r4gotkc5k8dmv 
        foreign key (process_listener_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_process_listener_names 
        add constraint FK_e3uy4vdqbely8oybcfc0ef7tn 
        foreign key (process_listener_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_process_listeners 
        add constraint FK_8b4m881ppfw6m13clxu4cp1o0 
        foreign key (formSection_object_id) 
        references ccm_core.formbuilder_formsections;

    alter table ccm_core.formbuilder_process_listeners 
        add constraint FK_a539g6h1xtndr87oov42wvdl4 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.formbuilder_remote_server_post_listener 
        add constraint FK_n4ymnx1dtjqedvta4e8hqfxpp 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_simple_email_listeners 
        add constraint FK_4phpnsgkmvblh5pgiej11aj9y 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_template_email_listeners 
        add constraint FK_cevp55p98seugf2368sc7yqqq 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.formbuilder_widget_labels 
        add constraint FK_tftgfd24vbwfhas20m20xt5e7 
        foreign key (widget_object_id) 
        references ccm_core.formbuilder_widgets;

    alter table ccm_core.formbuilder_widget_labels 
        add constraint FK_isff794p53xtpr1261vet6nhn 
        foreign key (object_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_widgets 
        add constraint FK_lv8wd5tad9t12m1qigj200hp2 
        foreign key (label_object_id) 
        references ccm_core.formbuilder_widget_labels;

    alter table ccm_core.formbuilder_widgets 
        add constraint FK_rgbe1klt8ktw2okc5lfbp7nkl 
        foreign key (object_id) 
        references ccm_core.formbuilder_components;

    alter table ccm_core.formbuilder_xml_email_listeners 
        add constraint FK_n6fdsiv02im6d6wyj5l799uh2 
        foreign key (object_id) 
        references ccm_core.formbuilder_process_listeners;

    alter table ccm_core.group_memberships 
        add constraint FK_gg62l9f6d82rl3h57r03y1f6y 
        foreign key (group_subject_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.group_memberships 
        add constraint FK_qm940kapbbc0ywyhkwh06wg48 
        foreign key (user_subject_id) 
        references ccm_core.ccm_users;

    alter table ccm_core.inits 
        add constraint FK_skqpgijaiv5idanah0e1hjoa 
        foreign key (required_by_id) 
        references ccm_core.inits;

    alter table ccm_core.lucene_documents 
        add constraint FK_n421djw91ggdmvsglk8t6tvk1 
        foreign key (created_by_party_id) 
        references ccm_core.subjects;

    alter table ccm_core.lucene_documents 
        add constraint FK_qa9tey3vy1xrpxkyqo9us25s3 
        foreign key (last_modified_by) 
        references ccm_core.subjects;

    alter table ccm_core.lucene_indexes 
        add constraint FK_7dqbase0oyxl83byea4hfdake 
        foreign key (host_id) 
        references ccm_core.hosts;

    alter table ccm_core.messages 
        add constraint FK_3l74b1gch8skj8t84emd65e3y 
        foreign key (in_reply_to_id) 
        references ccm_core.messages;

    alter table ccm_core.messages 
        add constraint FK_2tgrsfo79pwvrwk6lbdy32701 
        foreign key (sender_id) 
        references ccm_core.subjects;

    alter table ccm_core.messages 
        add constraint FK_ipx9bvlxhd3q9aqs3kmq2kayc 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.notifications 
        add constraint FK_k88btnwbdswv5ef360xxp8cn1 
        foreign key (digest_id) 
        references ccm_core.digests;

    alter table ccm_core.notifications 
        add constraint FK_fy4pjr1vlslocsi7d6vwku2yj 
        foreign key (message_id) 
        references ccm_core.messages;

    alter table ccm_core.notifications 
        add constraint FK_ajptmh33lr07i00e7j4pgheqe 
        foreign key (receiver_id) 
        references ccm_core.subjects;

    alter table ccm_core.notifications 
        add constraint FK_s4xvw4ebw2tq41i0kex5pyo5k 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.permissions 
        add constraint FK_aqw7r1c62xehp58uxwojun8xq 
        foreign key (creation_user_id) 
        references ccm_core.ccm_users;

    alter table ccm_core.permissions 
        add constraint FK_ilie616laommyrii7ecjbj521 
        foreign key (granted_privilege_id) 
        references ccm_core.ccm_privileges;

    alter table ccm_core.permissions 
        add constraint FK_g94li5wexu57n0mosdks1abuv 
        foreign key (grantee_id) 
        references ccm_core.subjects;

    alter table ccm_core.permissions 
        add constraint FK_r2p8pfvr7k5lth4bem2s0xqdv 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.portals 
        add constraint FK_mubhpxf8uf40wu2tc3ekkrqkc 
        foreign key (object_id) 
        references ccm_core.resources;

    alter table ccm_core.portlets 
        add constraint FK_i6o1tgre6iuc3yf7tk4jhmj6 
        foreign key (portal_id) 
        references ccm_core.portals;

    alter table ccm_core.portlets 
        add constraint FK_hvqa10v1thdr4riwt2unryk1y 
        foreign key (object_id) 
        references ccm_core.resources;

    alter table ccm_core.queue_items 
        add constraint FK_14jyt63f6cs84pangjcnphlps 
        foreign key (message_id) 
        references ccm_core.messages;

    alter table ccm_core.queue_items 
        add constraint FK_ojc2cc1yqd2htu88gxu16t11e 
        foreign key (receiver_id) 
        references ccm_core.subjects;

    alter table ccm_core.resource_descriptions 
        add constraint FK_ayx5lyxreydtjbvdugoff7mox 
        foreign key (object_id) 
        references ccm_core.resources;

    alter table ccm_core.resource_titles 
        add constraint FK_aer0mvcddder3150jlq0552nn 
        foreign key (object_id) 
        references ccm_core.resources;

    alter table ccm_core.resource_type_descriptions 
        add constraint FK_fp5rutbl3lvv5c322l87ma0ae 
        foreign key (resource_type_id) 
        references ccm_core.resource_types;

    alter table ccm_core.resources 
        add constraint FK_7bwjikili5hr55of80yvjlocc 
        foreign key (parent_object_id) 
        references ccm_core.resources;

    alter table ccm_core.resources 
        add constraint FK_2o0qb7opah9rt9ww8ydvp7cxv 
        foreign key (resourceType_resource_type_id) 
        references ccm_core.resource_types;

    alter table ccm_core.resources 
        add constraint FK_e6rvkh4kw8agtkvjqqdbiu0db 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.threads 
        add constraint FK_2d6ht9nsikaebakyppgtm8p2k 
        foreign key (root_id) 
        references ccm_core.messages;

    alter table ccm_core.threads 
        add constraint FK_jf5k6sucih0qp7l3ih2moeuha 
        foreign key (object_id) 
        references ccm_core.ccm_objects;

    alter table ccm_core.user_email_addresses 
        add constraint FK_m0hymqadkrd9o5eixeurjpifx 
        foreign key (user_id) 
        references ccm_core.ccm_users;

    alter table ccm_core.workflow_descriptions 
        add constraint FK_7grengdpx5d99jkyjlsa3pe6k 
        foreign key (workflow_id) 
        references ccm_core.workflows;

    alter table ccm_core.workflow_names 
        add constraint FK_sjqjarc88yvdrw3yd6swg7uqs 
        foreign key (workflow_id) 
        references ccm_core.workflows;

    alter table ccm_core.workflow_tasks 
        add constraint FK_mvuhbl6ikm44oxxtkv0s2y9iu 
        foreign key (workflow_id) 
        references ccm_core.workflows;

    alter table ccm_core.workflow_user_task_assigned_groups 
        add constraint FK_g58x45aybw2yjtwnr9b9itg6c 
        foreign key (assigned_group_id) 
        references ccm_core.ccm_groups;

    alter table ccm_core.workflow_user_task_assigned_groups 
        add constraint FK_jiogatex4mifbgji1og4rri9o 
        foreign key (user_task_id) 
        references ccm_core.workflow_user_tasks;

    alter table ccm_core.workflow_user_task_assigned_users 
        add constraint FK_h62r6cqjp2tdnhscfkgwfupwj 
        foreign key (assigned_user_id) 
        references ccm_core.ccm_users;

    alter table ccm_core.workflow_user_task_assigned_users 
        add constraint FK_ltihq91dcigqixb6ulhkphrix 
        foreign key (user_task_id) 
        references ccm_core.workflow_user_tasks;

    alter table ccm_core.workflow_user_tasks 
        add constraint FK_5nryb3wmian7oqttwqpa3wwll 
        foreign key (locking_user_id) 
        references ccm_core.ccm_users;

    alter table ccm_core.workflow_user_tasks 
        add constraint FK_s4tgjfnpvyhtpu0h4l72sht9g 
        foreign key (notification_sender) 
        references ccm_core.ccm_users;

    alter table ccm_core.workflow_user_tasks 
        add constraint FK_4nmt8xkbfog6dhq2mpt8m3skf 
        foreign key (workflow_id) 
        references ccm_core.workflows;

    create sequence hibernate_sequence start 1 increment 1;