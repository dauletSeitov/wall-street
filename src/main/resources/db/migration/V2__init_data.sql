INSERT INTO resources(id, created_at, updated_at, name, unit)
VALUES(1, '2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 'gold', 'gram');

INSERT INTO resources(id, created_at, updated_at, name, unit)
VALUES(2, '2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 'bitcoin', 'piece');

INSERT INTO resources(id, created_at, updated_at, name, unit)
VALUES(3, '2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 'diamond', 'carat');


INSERT INTO rates (created_at, updated_at, resource_id, price)
VALUES('2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 1, 30);

INSERT INTO rates (created_at, updated_at, resource_id, price)
VALUES('2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 2, 300);

INSERT INTO rates (created_at, updated_at, resource_id, price)
VALUES('2023-12-19 14:29:53.301', '2023-12-19 14:29:53.301', 3, 100);