insert into cohort(github_repository, name, alias)
values ('cohort27', 'cohort27', 'Cohort 27');

insert into cohort(github_repository, name, alias)
values ('cohort28', 'cohort28', 'Cohort 28');

insert into account(email, role, state, first_name, last_name)
values ('john.doe@example.com', 'STUDENT', 'NOT_CONFIRMED', 'John', 'Doe');

insert into account(email, role, state, first_name, last_name)
values ('expired.doe@example.com', 'STUDENT', 'NOT_CONFIRMED', 'Expired', 'Doe');

insert into student_cohort(user_id, cohort_id)
values ('1', '1');
insert into student_cohort(user_id, cohort_id)
values ('1', '2');

insert into student_cohort(user_id, cohort_id)
values ('2', '1');
insert into student_cohort(user_id, cohort_id)
values ('2', '2');


insert into confirmation_code(expired_time, uuid, user_id)
values ('2024-10-27 14:30:00','6c2f764a-9f9c-4856-8bad-61097e099e7c', '1');

insert into confirmation_code(expired_time, uuid, user_id)
values ('2024-10-02 14:30:00','26c2f764a-9f9c-4856-8bad-61097e099e7', '2');

