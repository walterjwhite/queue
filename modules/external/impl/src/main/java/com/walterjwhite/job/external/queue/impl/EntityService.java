package com.walterjwhite.job.external.queue.impl;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.encryption.api.service.CompressionService;
import com.walterjwhite.encryption.api.service.EncryptionService;
import com.walterjwhite.serialization.api.service.SerializationService;
import java.io.*;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityService {
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityService.class);
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

  public AbstractEntity read(byte[] data)
      throws InvalidAlgorithmParameterException, InvalidKeyException, IOException,
          ClassNotFoundException, NoSuchAlgorithmException {
    final byte[] plaintextData = encryptionService.decrypt(data);
    LOGGER.info("decrypted message:" + plaintextData.length);
    final byte[] uncompressedData = compressionService.decompress(plaintextData);
    LOGGER.info("decompressed message:" + uncompressedData.length);

    return (getMessage(uncompressedData));
  }

  protected Class getEntityClass(ByteArrayInputStream byteArrayInputStream)
      throws IOException, ClassNotFoundException {
    final StringBuilder buffer = new StringBuilder();
    int read;
    while ((read = byteArrayInputStream.read()) != '\n') {
      buffer.append((char) read);
    }

    return (Class.forName(buffer.toString()));
  }

  public AbstractEntity getMessage(final byte[] uncompressedData)
      throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
    return (getMessage(uncompressedData, true));
  }

  public AbstractEntity getMessage(final byte[] uncompressedData, boolean validate)
      throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
    try (final ByteArrayInputStream bais = new ByteArrayInputStream(uncompressedData)) {

      final Class<? extends AbstractEntity> entityClass = getEntityClass(bais);
      try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        int read;
        while ((read = bais.read()) != -1) {
          byteArrayOutputStream.write(read);
        }

        byteArrayOutputStream.flush();
        if (byteArrayOutputStream == null || byteArrayOutputStream.size() == 0)
          throw (new RuntimeException("Message was empty, unable to read"));

        return ((AbstractEntity)
            serializationService.deserialize(
                byteArrayOutputStream.toByteArray(), /*AbstractEntity.class*/ entityClass));
      }
    }
  }

  public AbstractEntity readRaw(byte[] data)
      throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
    return (getMessage(data));
  }

  public byte[] write(AbstractEntity entity) throws Exception {
    final byte[] serializedMessage = serializationService.serialize(entity);
    LOGGER.info("serializedMessage:" + serializedMessage.length);

    try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      // avoid
      baos.write((entity.getClass().getName() + "\n").getBytes(Charset.defaultCharset()));
      baos.write(serializedMessage);
      baos.flush();

      final byte[] compressedMessage = compressionService.compress(baos.toByteArray());
      LOGGER.info("compressedMessage:" + compressedMessage.length);

      // TODO: support separate IVs / client / session?
      // TODO: refactor this
      final byte[] encryptedContents = encryptionService.encrypt(compressedMessage);
      LOGGER.info("encryptedContents:" + encryptedContents.length);

      return (encryptedContents);
    }
  }
}
