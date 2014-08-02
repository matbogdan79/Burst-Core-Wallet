/**
 * @depends {nrs.js}
 * @depends {nrs.modals.js}
 */
var NRS = (function(NRS, $, undefined) {
	$("#account_details_modal").on("show.bs.modal", function(e) {
		$("#account_details_modal_balance").show();

		if (NRS.accountInfo.errorCode && NRS.accountInfo.errorCode != 5) {
			$("#account_balance_table").hide();
			//todo
			$("#account_balance_warning").html(String(NRS.accountInfo.errorDescription).escapeHTML()).show();
		} else {
			$("#account_balance_warning").hide();

			if (NRS.accountInfo.errorCode && NRS.accountInfo.errorCode == 5) {
				$("#account_balance_balance, #account_balance_unconfirmed_balance, #account_balance_effective_balance, #account_balance_guaranteed_balance").html("0 NXT");
				$("#account_balance_public_key").html(String(NRS.publicKey).escapeHTML());
				$("#account_balance_account_rs").html(String(NRS.accountRS).escapeHTML());
			} else {
				$("#account_balance_balance").html(NRS.formatAmount(new BigInteger(NRS.accountInfo.balanceNQT)) + " NXT");
				$("#account_balance_unconfirmed_balance").html(NRS.formatAmount(new BigInteger(NRS.accountInfo.unconfirmedBalanceNQT)) + " NXT");
				$("#account_balance_effective_balance").html(NRS.formatAmount(NRS.accountInfo.effectiveBalanceNXT) + " NXT");
				$("#account_balance_guaranteed_balance").html(NRS.formatAmount(new BigInteger(NRS.accountInfo.guaranteedBalanceNQT)) + " NXT");

				$("#account_balance_public_key").html(String(NRS.accountInfo.publicKey).escapeHTML());
				$("#account_balance_account_rs").html(String(NRS.accountInfo.accountRS).escapeHTML());

				if (!NRS.accountInfo.publicKey) {
					$("#account_balance_public_key").html("/");
					$("#account_balance_warning").html($.t("no_public_key_warning") + " " + $.t("public_key_actions")).show();
				}
			}
		}
	});

	$("#account_details_modal ul.nav li").click(function(e) {
		e.preventDefault();

		var tab = $(this).data("tab");

		$(this).siblings().removeClass("active");
		$(this).addClass("active");

		$(".account_details_modal_content").hide();

		var content = $("#account_details_modal_" + tab);

		content.show();
	});

	$("#account_details_modal").on("hidden.bs.modal", function(e) {
		$(this).find(".account_details_modal_content").hide();
		$(this).find("ul.nav li.active").removeClass("active");
		$("#account_details_balance_nav").addClass("active");
	});

	return NRS;
}(NRS || {}, jQuery));