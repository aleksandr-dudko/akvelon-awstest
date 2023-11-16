package com.akvelon.awstest.dao;

import com.akvelon.awstest.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
    Image findByName(String name);
}
