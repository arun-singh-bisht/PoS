package com.posfone.promote.posfone.stripe;

import java.util.HashMap;
import java.util.Map;

import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.util.Log;
import com.google.common.collect.Maps;
import com.posfone.promote.posfone.rest.ApiClient;
/*import com.posfone.promote.posfone.rest.Constants;
import com.stripe.Stripe;
import com.stripe.android.Stripe;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;*/
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.ApiException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;

/**
 * Provides functionality to accept payements using Stripe API.
 * 
 * @author excelsior
 * 
 */
public class  StripePaymentService
{
	/**
	 * Debit the amount from the credit card using Stripe API.
	 *
	 * @return
	 */
	public static final boolean debit(com.stripe.android.model.Token token,String total,String txn_id,String package_name,String package_id,String name,String number)
	{
		int SDK_INT = android.os.Build.VERSION.SDK_INT;
		if (SDK_INT > 8) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			com.stripe.Stripe.apiKey = ApiClient.STRIPE_KEY;
			String s = total;
			double d = Double.parseDouble(s);
			int i = (int) d;

			boolean isDebitSuccess = true;
			Exception err = null;
			Charge charge = null;

			try {
               System.out.println("payment started  ");
				Map<String, Object> params = new HashMap<>();
				params.put("amount", i*100);
				params.put("currency", "usd");
				//params.put("customer", name);
				params.put("description", package_name);
				params.put("source", token.getId());
				Map<String, String> metadata = new HashMap<>();
				metadata.put("order_id", txn_id);
				metadata.put("package_id",package_id);
				metadata.put("pay729_number", number);
				metadata.put("user_name",name);
				params.put("metadata", metadata);

				charge = Charge.create(params);
			} catch (AuthenticationException e) {
				isDebitSuccess = false;
				err = e;
				Log.e("", "Auth exception");
			} catch (InvalidRequestException e) {
				isDebitSuccess = false;
				err = e;
				Log.e("", "Invalid Request exception");
			} catch (CardException e) {
				isDebitSuccess = false;
				err = e;
				Log.e("", "Card exception");
			} catch (ApiConnectionException e) {
				e.printStackTrace();
			} catch (ApiException e) {
				e.printStackTrace();
			} catch (StripeException e) {
				e.printStackTrace();
			} catch (NetworkOnMainThreadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (isDebitSuccess) {
			/*	Log.i("StripePaymentService", "Credit card debited successfully for " + amount + " "
						+ currency + "Charge : ");
			*/} else {
				/*Log.e("StripePaymentService", "Failed to debit credit card for " + amount + " "
						+ currency);
*/				err.printStackTrace();
			}
		}
			return true;

		}

}
