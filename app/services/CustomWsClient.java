package services;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.libs.ws.*;

@Singleton
public class CustomWsClient implements WSBodyReadables, WSBodyWritables {
  private final WSClient ws;

  @Inject
  public CustomWsClient(WSClient ws) {
    this.ws = ws;
  }

  public WSRequest request(String url) {
    return ws.url(url);
  }
}