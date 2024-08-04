insert into cohort(github_repository, name, alias)
values ('cohort27', 'cohort27', 'Cohort 27');

insert into cohort(github_repository, name, alias)
values ('cohort28', 'cohort28', 'Cohort 28');

insert into account(email, role, state, first_name, last_name)
values ('john.doe@example.com', 'STUDENT', 'CONFIRMED', 'John', 'Doe');

insert into student_cohort(user_id, cohort_id)
values ('1', '1');
insert into student_cohort(user_id, cohort_id)
values ('1', '2');