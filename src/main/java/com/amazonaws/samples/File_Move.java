package com.amazonaws.samples;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;

public class File_Move 
{
	public static void main(String[] args) throws IOException
	{
		String clientRegion = "ap-south-1";
        String srcbucketName = "storagebigbucket";
        String key = "POC.txt";
        String destbucketName="managedata";
        String objectKey="POC.txt";
        String restore="storagebigbucket/archive/"; 
        S3Object fullObject = null;
        try 
        {
        	
            
        	BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIARXBJSPVLQJESECGS", "BZCKVq0Xm9whMt1Ij+yD2RaaVhSy52fE2f33dzFR");
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            		.withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
            fullObject = s3Client.getObject(new GetObjectRequest(srcbucketName, key));
            //s3Client.putObject(destbucketName, objectKey,"Upload Object" );
            
            ObjectMetadata metadata = new ObjectMetadata();
            
            
            S3Object s3object = s3Client.getObject(srcbucketName, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            
            
            byte[] bytes = IOUtils.toByteArray(inputStream);
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest putObjectRequest = new PutObjectRequest(destbucketName, objectKey, byteArrayInputStream, metadata);
            s3Client.putObject(putObjectRequest);
            
            
           /* S3Object s3object2 = s3Client.getObject(srcbucketName, key);
            S3ObjectInputStream inputStream2 = s3object2.getObjectContent();
            
            
            byte[] bytes2 = IOUtils.toByteArray(inputStream2);
            metadata.setContentLength(bytes2.length);
            ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bytes2);
            PutObjectRequest putObjectRequest2 = new PutObjectRequest(restore, objectKey, byteArrayInputStream2, metadata);
            s3Client.putObject(putObjectRequest2);*/
            
            
            
            	 ObjectListing objects = s3Client.listObjects(destbucketName); 
                 do 
                 {
                         for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) 
                         {
                                 System.out.println(objectSummary.getKey() + "\t" +
                                         objectSummary.getSize() + "\t" +
                                         StringUtils.fromDate(objectSummary.getLastModified()));
                                 if((objectSummary.getKey()).equals(objectKey))
                                 {
                                	 //Check it is work if not then comment this and use above comment code
                                	 S3Object s3object2 = s3Client.getObject(srcbucketName, key);
                                     S3ObjectInputStream inputStream2 = s3object2.getObjectContent();
                                     
                                     
                                     byte[] bytes2 = IOUtils.toByteArray(inputStream2);
                                     metadata.setContentLength(bytes2.length);
                                     ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bytes2);
                                     PutObjectRequest putObjectRequest2 = new PutObjectRequest(restore, objectKey, byteArrayInputStream2, metadata);
                                     s3Client.putObject(putObjectRequest2);
                                                              
                                	 s3Client.deleteObject(new DeleteObjectRequest(srcbucketName, key));
                                	 
                                 }
                         }
                         objects = s3Client.listNextBatchOfObjects(objects);
                 } while (objects.isTruncated());
                 
                 System.out.println("Download Object");
            
        }
        catch(AmazonServiceException e) 
        {
            e.printStackTrace();
        }
        catch(SdkClientException e) 
        {
            e.printStackTrace();
        }

	}
}
