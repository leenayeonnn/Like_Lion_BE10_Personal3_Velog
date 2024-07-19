DROP SCHEMA project3_velog;
CREATE SCHEMA project3_velog;

USE project3_velog;

CREATE TABLE `users`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username`          VARCHAR(50)  NOT NULL UNIQUE,
    `password`          VARCHAR(100) NOT NULL,
    `name`              VARCHAR(100) NOT NULL,
    `email`             VARCHAR(100) NOT NULL UNIQUE,
    `profile_url`       VARCHAR(512) NOT NULL DEFAULT '/image/profile/common/common-profile.png',
    `registration_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `roles`
(
    `id`   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE `user_roles`
(
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_token
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    value   VARCHAR(255) NOT NULL
);

CREATE TABLE `blogs`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NOT NULL DEFAULT '',
    `user_id`     BIGINT       NOT NULL
);

CREATE TABLE `posts`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title`             VARCHAR(255) NOT NULL,
    `content`           TEXT         NOT NULL,
    `registration_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_public`         BOOL         NOT NULL,
    `is_published`      BOOL         NOT NULL,
    `main_img_url`      TEXT         NULL,
    `blog_id`           BIGINT       NOT NULL,
    `view`              BIGINT       NULL     DEFAULT 0
);

CREATE TABLE `tags`
(
    `id`   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL
);

CREATE TABLE `post_tags`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT,
    `tag_id`  BIGINT NOT NULL,
    `post_id` BIGINT NOT NULL
);

CREATE TABLE `series`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`    VARCHAR(100) NOT NULL,
    `blog_id` BIGINT       NOT NULL
);

CREATE TABLE `post_series`
(
    `id`        BIGINT PRIMARY KEY AUTO_INCREMENT,
    `series_id` BIGINT NOT NULL,
    `post_id`   BIGINT NOT NULL
);

CREATE TABLE `likes`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL
);

CREATE TABLE `follows`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `following_user_id` BIGINT NOT NULL,
    `followed_user_id`  BIGINT NOT NULL
);

CREATE TABLE `comments`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `content`           VARCHAR(200) NOT NULL,
    `registration_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `parent_id`         BIGINT       NULL,
    `post_id`           BIGINT       NOT NULL,
    `user_id`           BIGINT       NULL
);

ALTER TABLE `user_roles`
    ADD CONSTRAINT `FK_users_TO_user_roles_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `user_roles`
    ADD CONSTRAINT `FK_roles_TO_user_roles_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE;

ALTER TABLE `refresh_token`
    ADD CONSTRAINT `FK_users_TO_refresh_token_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `blogs`
    ADD CONSTRAINT `FK_users_TO_blogs_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `posts`
    ADD CONSTRAINT `FK_blogs_TO_posts_1` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`id`) ON DELETE CASCADE;

ALTER TABLE `post_tags`
    ADD CONSTRAINT `FK_tags_TO_post_tags_1` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE;

ALTER TABLE `post_tags`
    ADD CONSTRAINT `FK_posts_TO_post_tags_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE;

ALTER TABLE `post_series`
    ADD CONSTRAINT `FK_series_TO_post_series_1` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE CASCADE;

ALTER TABLE `post_series`
    ADD CONSTRAINT `FK_posts_TO_post_series_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE;

ALTER TABLE `series`
    ADD CONSTRAINT `FK_blogs_TO_series_1` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`id`) ON DELETE CASCADE;

ALTER TABLE `likes`
    ADD CONSTRAINT `FK_posts_TO_likes_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE;

ALTER TABLE `likes`
    ADD CONSTRAINT `FK_users_TO_likes_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `follows`
    ADD CONSTRAINT `FK_users_TO_follows_1` FOREIGN KEY (`following_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `follows`
    ADD CONSTRAINT `FK_users_TO_follows_2` FOREIGN KEY (`followed_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `comments`
    ADD CONSTRAINT `FK_posts_TO_comments_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE;

ALTER TABLE `comments`
    ADD CONSTRAINT `FK_users_TO_comments_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

INSERT INTO roles (name)
VALUES ('USER'),
       ('ADMIN');