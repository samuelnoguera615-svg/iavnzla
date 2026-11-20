#ifndef SECURITY_UTIL_H
#define SECURITY_UTIL_H

int encode_sale_reference(int seller_code, int article_id);
int validate_vendor_code(int vendor_code, int expected_vendor_code);
int calculate_tier_score(int total_sales, double monthly_amount);

#endif
