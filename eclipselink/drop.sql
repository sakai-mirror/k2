ALTER TABLE phone DROP CONSTRAINT FK_phone_oid
ALTER TABLE phone DROP CONSTRAINT FK_phone_person_id
ALTER TABLE person_address DROP CONSTRAINT person_address_oid
ALTER TABLE person_address DROP CONSTRAINT prsnaddressprsonid
ALTER TABLE template_params DROP CONSTRAINT tmpltparamsctvtyid
ALTER TABLE person DROP CONSTRAINT personbody_type_id
ALTER TABLE person DROP CONSTRAINT FK_person_name_id
ALTER TABLE person DROP CONSTRAINT person_address_id
ALTER TABLE activity_media DROP CONSTRAINT ctivitymediamdiaid
ALTER TABLE activity_media DROP CONSTRAINT ctvtymediactvityid
ALTER TABLE person_organization DROP CONSTRAINT prsnrgnzationprsnd
ALTER TABLE person_organization DROP CONSTRAINT prsonorganizationd
ALTER TABLE im DROP CONSTRAINT FK_im_person_id
ALTER TABLE im DROP CONSTRAINT FK_im_oid
ALTER TABLE person_account DROP CONSTRAINT person_account_oid
ALTER TABLE person_account DROP CONSTRAINT prsnaccountprsonid
ALTER TABLE email DROP CONSTRAINT FK_email_person_id
ALTER TABLE email DROP CONSTRAINT FK_email_oid
ALTER TABLE organizational_address DROP CONSTRAINT rgnztionaladdressd
ALTER TABLE organizational_address DROP CONSTRAINT rgnztnlddrsrgnztnd
ALTER TABLE url DROP CONSTRAINT FK_url_oid
ALTER TABLE url DROP CONSTRAINT FK_url_person_id
ALTER TABLE photo DROP CONSTRAINT FK_photo_person_id
ALTER TABLE photo DROP CONSTRAINT FK_photo_oid
ALTER TABLE person_properties DROP CONSTRAINT personpropertiesid
ALTER TABLE person_properties DROP CONSTRAINT prsnprpertiesprsnd
DROP TABLE phone
DROP TABLE list_field
DROP TABLE person_address
DROP TABLE activity
DROP TABLE template_params
DROP TABLE account
DROP TABLE message
DROP TABLE address
DROP TABLE person
DROP TABLE media_item
DROP TABLE activity_media
DROP TABLE person_organization
DROP TABLE im
DROP TABLE person_account
DROP TABLE email
DROP TABLE body_type
DROP TABLE organizational_address
DROP TABLE url
DROP TABLE photo
DROP TABLE name
DROP TABLE person_properties
DROP TABLE organization
