package services;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JPARepository {
  private JPAApi jpaApi;

  @Inject
  public JPARepository(JPAApi api) {
    this.jpaApi = api;
  }
}