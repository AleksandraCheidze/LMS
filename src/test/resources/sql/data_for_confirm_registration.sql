insert into cohort(github_repository, name, alias)
values ('cohort27', 'cohort27', 'Cohort 27');

insert into account(email, role, state, first_name, last_name)
values ('john.doe@example.com', 'STUDENT', 'NOT_CONFIRMED', 'John', 'Doe');

insert into student_cohort(user_id, cohort_id)
values ('1', '1');

insert into confirmation_code(expired_time, uuid, user_id)
values (DATEADD('DAY', 1, NOW()), '998bf356-8ad6-4985-817e-bca1821fbe0b', '1');