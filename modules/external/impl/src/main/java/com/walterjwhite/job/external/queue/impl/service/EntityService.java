package com.walterjwhite.job.external.queue.impl.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.encryption.api.service.CompressionService;
import com.walterjwhite.encryption.service.EncryptionService;
import com.walterjwhite.serialization.api.service.SerializationService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.inject.Inject;

public class EntityService {
  protected final EncryptionService encryptionService;
  protected final CompressionService compressionService;
  protected final SerializationService serializationService;

  @Inject
  public EntityService(
      EncryptionService encryptionService,
      CompressionService compressionService,
      SerializationService serializationService) {
    super();
    this.encryptionService = encryptionService;
    this.compressionService = compressionService;
    this.serializationService = serializationService;
  }

  public AbstractEntity read(final InputStream inputStream)
      throws IOException, ClassNotFoundException {
    return (getMessage(
        compressionService.getDecompressionStream(
            encryptionService.getDecryptionStream(inputStream))));
  }

  protected Class getEntityClass(InputStream inputStream)
      throws ClassNotFoundException, IOException {
    final StringBuilder buffer = new StringBuilder();
    int read;
    while ((read = inputStream.read()) != '\n') {
      buffer.append((char) read);
    }

    return (Class.forName(buffer.toString()));
  }

  public AbstractEntity getMessage(final InputStream inputStream)
      throws IOException, ClassNotFoundException {
    return (getMessage(inputStream, true));
  }

  public AbstractEntity getMessage(final InputStream inputStream, boolean validate)
      throws IOException, ClassNotFoundException {
    final Class<? extends AbstractEntity> entityClass = getEntityClass(inputStream);

    return ((AbstractEntity) serializationService.deserialize(inputStream, entityClass));
  }

  public void write(AbstractEntity entity, OutputStream outputStream) throws Exception {
    // TODO: support separate IVs / client / session?
    // TODO: refactor this
    try (final OutputStream encryptedOutputStream =
        encryptionService.getEncryptionStream(
            compressionService.getCompressionStream(outputStream))) {
      encryptedOutputStream.write(
          (entity.getClass().getName() + "\n").getBytes(Charset.defaultCharset()));
      serializationService.serialize(entity, encryptedOutputStream);
      encryptedOutputStream.flush();
    }
  }
}
