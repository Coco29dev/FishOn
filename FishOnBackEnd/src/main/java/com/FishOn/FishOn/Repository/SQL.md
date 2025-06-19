# Requête `SQL` générées par `Spring Data JPA`

## UserRepository
`findByUserName`
```sql
SELECT * FROM user_model
WHERE user_name = ?
```

`findByEmail`
```sql
SELECT * FROM user_model
WHERE email = ?
```

`existsByUserName`
```sql
SELECT COUNT(*) > 0 FROM user_model 
WHERE user_name = ?
```

`existsByEmail`
```sql
SELECT COUNT(*) > 0 FROM user_model 
WHERE email = ?
```

## PostRepository
Requête `SQL` générées par `Spring Data JPA`.

`findByUserUserName`
```sql
SELECT p.* FROM post_model p 
INNER JOIN user_model u ON p.user_id = u.id 
WHERE u.user_name = ?
```

`findByUserId`
```sql
SELECT * FROM post_model 
WHERE user_id = ?
```

`findByFishName`
```sql
SELECT * FROM post_model 
WHERE fish_name = ?
```

`findByLocation`
```sql
SELECT * FROM post_model 
WHERE location = ?
```

`existsByFishName`
```sql
SELECT COUNT(*) > 0 FROM post_model 
WHERE fish_name = ?
```

`existsByLocation`
```sql
SELECT COUNT(*) > 0 FROM post_model 
WHERE location = ?
```

## Commentrepository
Requête `SQL` générées par `Spring Data JPA`.

`findByUserId`
```sql
SELECT * FROM comment_model 
WHERE user_id = ?
```

`findByPostId`
```sql
SELECT * FROM comment_model 
WHERE post_id = ?
```

# Méthodes héritées de `JPA Repository`

## save(T entity)
```sql
-- Pour une nouvelle entité
INSERT INTO table_name (column1, column2, ...) 
VALUES (?, ?, ...)

-- Pour une mise à jour
UPDATE table_name 
SET column1 = ?, column2 = ?, ... 
WHERE id = ?
```

## findById
```sql
SELECT * FROM table_name 
WHERE id = ?
```

## findAll()
```sql
SELECT * FROM table_name
```

## deleteById()
```sql
DELETE FROM table_name 
WHERE id = ?
```

## count()
```sql
SELECT COUNT(*) FROM table_name
```

## existsById
```sql
SELECT COUNT(*) > 0 FROM table_name 
WHERE id = ?
```
