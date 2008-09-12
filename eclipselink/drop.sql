ALTER TABLE phone DROP FOREIGN KEY FK_phone_oid
ALTER TABLE phone DROP FOREIGN KEY FK_phone_person_id
ALTER TABLE person_address DROP FOREIGN KEY FK_person_address_person_id
ALTER TABLE person_address DROP FOREIGN KEY FK_person_address_oid
ALTER TABLE template_params DROP FOREIGN KEY FK_template_params_activity_id
ALTER TABLE person DROP FOREIGN KEY FK_person_address_id
ALTER TABLE person DROP FOREIGN KEY FK_person_name_id
ALTER TABLE person DROP FOREIGN KEY FK_person_body_type_id
ALTER TABLE activity_media DROP FOREIGN KEY FK_activity_media_activity_id
ALTER TABLE activity_media DROP FOREIGN KEY FK_activity_media_media_id
ALTER TABLE person_organization DROP FOREIGN KEY FK_person_organization_oid
ALTER TABLE person_organization DROP FOREIGN KEY FK_person_organization_person_id
ALTER TABLE im DROP FOREIGN KEY FK_im_person_id
ALTER TABLE im DROP FOREIGN KEY FK_im_oid
ALTER TABLE person_account DROP FOREIGN KEY FK_person_account_oid
ALTER TABLE person_account DROP FOREIGN KEY FK_person_account_person_id
ALTER TABLE organizational_address DROP FOREIGN KEY FK_organizational_address_organization_id
ALTER TABLE organizational_address DROP FOREIGN KEY FK_organizational_address_oid
ALTER TABLE email DROP FOREIGN KEY FK_email_person_id
ALTER TABLE email DROP FOREIGN KEY FK_email_oid
ALTER TABLE url DROP FOREIGN KEY FK_url_oid
ALTER TABLE url DROP FOREIGN KEY FK_url_person_id
ALTER TABLE photo DROP FOREIGN KEY FK_photo_person_id
ALTER TABLE photo DROP FOREIGN KEY FK_photo_oid
ALTER TABLE person_properties DROP FOREIGN KEY FK_person_properties_person_id
ALTER TABLE person_properties DROP FOREIGN KEY FK_person_properties_oid
DROP TABLE list_field
DROP TABLE phone
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
DROP TABLE organizational_address
DROP TABLE email
DROP TABLE url
DROP TABLE body_type
DROP TABLE photo
DROP TABLE name
DROP TABLE person_properties
DROP TABLE organization
