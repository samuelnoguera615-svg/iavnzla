#include "security_util.h"

int encode_sale_reference(int seller_code, int article_id) {
    return (seller_code * 131) + (article_id * 17);
}

int validate_vendor_code(int vendor_code, int expected_vendor_code) {
    return vendor_code == expected_vendor_code;
}

int calculate_tier_score(int total_sales, double monthly_amount) {
    int base_score = total_sales * 3;
    int volume_score = (int)(monthly_amount / 100.0);
    return base_score + volume_score;
}
