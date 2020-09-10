<?php

defined('BASEPATH') OR exit('No direct script access allowed');

$CI =& get_instance();
$CI->load->database();

$config['googleplus']['application_name'] = 'DOVBS';
$config['googleplus']['client_id']        = $CI->config->item('site_settings')->google_client_id;
$config['googleplus']['client_secret']    = $CI->config->item('site_settings')->google_client_secret;
$config['googleplus']['redirect_uri']     = 'http://localhost/dvbs-version2/DOVBS-V2/en/Googlelogin/';
$config['googleplus']['api_key']          = 'dovbs2-189812';
$config['googleplus']['scopes']           = array();

