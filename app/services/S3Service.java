package services;

import com.typesafe.config.Config;
import controllers.StaticAssets;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Singleton
public class S3Service {
    private static final String BUCKET = "{bucket}";
    private static final String S3LARGE = "large/";
    private static final String S3MEDIUM = "medium/";
    private static final String S3LOGODIR = "logo/";
    private static final String S3BLOGDIR = "blog/";
    public static String TMPMAINDIR = "/external/images/";
    public static String TMPLARGEDIR = TMPMAINDIR + "large/";
    public static String TMPMEDIUMDIR = TMPMAINDIR + "medium/";
    public static String TMPLOGODIR = TMPMAINDIR + "logo/";
    public static String TMPBLOGDIR = TMPMAINDIR + "blog/";
    private static S3Client s3;

    static {
        if(Singletons.environment.isDev()) {
            String TMPMAINDIR = "/Users/seyi/Dev/external/c_images/";
            S3Service.TMPMAINDIR = TMPMAINDIR;
            S3Service.TMPLARGEDIR = TMPMAINDIR + "large/";
            S3Service.TMPMEDIUMDIR = TMPMAINDIR + "medium/";
            S3Service.TMPLOGODIR = TMPMAINDIR + "logo/";
            S3Service.TMPBLOGDIR = TMPMAINDIR + "blog/";

            System.out.println("LOADED DEV IMAGES PATH");
        }
    }

    @Inject
    public S3Service(Config config) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                config.getString("aws_s3_key"),
                config.getString("aws_s3_secret"));

        Region region = Region.US_EAST_1;
        s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(region)
                .build();
    }

    public static int storeOrigin(File original, String fileName) throws Exception {
        File origin = new File(TMPMAINDIR + fileName);
        SimpleImageInfo info = new SimpleImageInfo(original);
        int width = info.getWidth();

        if(width > 800) {
            Thumbnails.of(original)
                    .width(800)
                    .toFile(origin);
        } else {
            Thumbnails.of(original)
                    .width(width)
                    .toFile(origin);
        }
        return width;
    }

    public static void storePicture(File original, String fileName, int width, boolean process) throws Exception {

        File large = new File(TMPLARGEDIR + fileName);
        File medium = new File(TMPMEDIUMDIR + fileName);

        if(process) {
            BufferedImage watermarkImage = ImageIO.read(Objects.requireNonNull(Singletons.environment.resourceAsStream("water.png")));

            if (width > 800) {
                Thumbnails.of(original)
                        .width(800)
                        .watermark(Positions.CENTER, watermarkImage, 0.5f)
                        .toFile(large);
            } else {
                Thumbnails.of(original)
                        .width(width)
                        .watermark(Positions.CENTER, watermarkImage, 0.5f)
                        .toFile(large);
            }

            Thumbnails.of(original)
                    .size(300, 260)
                    .toFile(medium);
        } else {
            FileUtils.copyFile(original, large);
            FileUtils.copyFile(original, medium);
        }

        putS3Object(S3LARGE+fileName, large);
        putS3Object(S3MEDIUM+fileName, medium);

        try {
            large.delete();
            medium.delete();
            original.delete();
        } catch (Exception e) {e.printStackTrace();}

    }

    public static String putS3Object(String objectKey, File file) {
        try {

            Map<String, String> metadata = new HashMap<>();
            //metadata.put("file_id", fileId.toString());

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .bucket(BUCKET)
                    .key(objectKey)
                    //.metadata(metadata)
                    .build();

            PutObjectResponse response = s3.putObject(putOb,
                    RequestBody.fromBytes(getObjectFile(file)));

            return response.eTag();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    private static byte[] getObjectFile(File file) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

    public static byte[] getObjectBytes(String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(BUCKET)
                    .build();

            //objectRequest.responseContentType();
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();


            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        String ext = "";
        int mid = fileName.lastIndexOf(".");

        ext = fileName.substring(mid + 1, fileName.length());
        if("pdf".equalsIgnoreCase(ext)){
            return "png";
        }
        return ext.toLowerCase();
    }

    public static String rotateImage(String filePath, String dir, String newName, Double angle) {
        try {
            String path = TMPMAINDIR + filePath;
            File file = new File(path);
            if(!file.exists()) {
                URL url = new URL(StaticAssets.getImg(filePath));
                FileUtils.copyURLToFile(url, file);
            }

            ConvertCmd cmd = new ConvertCmd(true);
            IMOperation op = new IMOperation();
            op.addImage(path);
            op.rotate(angle);
            op.addImage(path);
            cmd.run(op);

            String fileNewPath = dir + "/" + newName;

            putS3Object(fileNewPath, file);

            return newName;
        } catch(Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    public static String storeLogo(File original, String fileName, boolean process) throws Exception {

        File main = new File(TMPLOGODIR + fileName);

        if(process) {
            SimpleImageInfo info = new SimpleImageInfo(original);
            int width = info.getWidth();
            if (width > 400) {
                Thumbnails.of(original)
                        .width(400)
                        .toFile(main);
            } else {
                Thumbnails.of(original)
                        .width(width)
                        .toFile(main);
                //original.renameTo(main);
            }
        } 

        putS3Object(S3LOGODIR + fileName, main);

        try {
            main.delete();
            original.delete();
        } catch (Exception e) {e.printStackTrace();}

        return fileName;
    }


    public static String storeBlogImage(File original, String fileName, boolean process) throws Exception {
        /*File main = new File(TMPBLOGDIR + fileName);

        if(process) {
            SimpleImageInfo info = new SimpleImageInfo(original);
            int width = info.getWidth();
            if (width > 400) {
                Thumbnails.of(original)
                        .width(400)
                        .toFile(main);
            } else {
                Thumbnails.of(original)
                        .width(width)
                        .toFile(main);
                //original.renameTo(main);
            }
        }*/

        putS3Object(S3BLOGDIR + fileName, original);

        return fileName;
    }
}