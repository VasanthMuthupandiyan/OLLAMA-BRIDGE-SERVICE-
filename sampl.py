import requests
import time
import logging
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s"
)

URL = "http://192.168.1.100:7483/api/chat"

HEADERS = {
    "accept": "*/*",
    "X-API-KEY": "0acfd19c9aad3a4eb46a02cef896e6f35f7ea9e4cb65b11f65a8521b85d6ca97",
    "Content-Type": "application/json"
}

NUM_REQUESTS = 5


def build_payload(request_id):
    ocr_samples = {
        1: """
PUMP STRT PB-Pmp Strt Push Buttn
N C-Normly Clsd
MTR OLTRP-Mtr Ovrld Trp
EMRG STP NC-Emrgncy Stp Normly Clsd
CNTCTR AUX NO-Cntctr Aux Normly Opn
""",
        2: """
MCCB INCMR-Moldd Case Circut Brkr Incmr
XFMR SEC BRKR-Trnsfrmr Secndry Brkr
PLC DO MOD-Prog Logic Cntrlr Dig Out Mod
ESD LOOP NC-Emergncy Shutdwn Lop Normly Clsd
FLW SWTCH NO-Flw Swtch Normly Opn
""",
        3: """
VLV POS FBK-Valv Pos Fdback
PRSR SWTCH NC-Prsure Swtch Normly Clsd
TMP ALRM HI-Temp Alrm Hgh
SOL VLV CMD-Solnoid Valv Cmnd
MTR RUN STS-Mtr Run Stts
""",
        4: """
DG STRT CMD-Dsl Gen Strt Cmnd
BAT CHGR FLT-Bat Chgr Flt
GEN AVLBL STS-Gen Availbl Stts
FUEL LVL LO-Ful Lvl Low
ATS POS FBK-Auto Trnsfr Swtch Pos Fdback
""",
        5: """
LTG PNL FEDR-Lghtng Pnl Fdr
UPS BYP SWTCH-Unintrptbl Pwr Suply Byp Swtch
PWR FAIL ALM-Pwr Fal Alrm
CAB FAN RUN-Cabnt Fan Rn
EARTH FLT IND-Erth Flt Indctr
"""
    }

    prompt = f"""
Please clean the following OCR extracted electrical/control panel text.

Instructions:
1. The OCR text is intentionally noisy and contains OCR errors.
2. Correct spelling mistakes and OCR mistakes.
3. Expand abbreviations using the mapping below.
4. Return the cleaned text in a readable format.
5. Preserve the original engineering meaning.
6. Return ONLY the cleaned output.

Abbreviation Mapping:
{{
    "nc": "normally closed",
    "no": "normally open",
    "cmd": "command",
    "sts": "status",
    "flt": "fault",
    "fbk": "feedback",
    "strt": "start",
    "swtch": "switch",
    "prsr": "pressure",
    "tmp": "temperature",
    "vlv": "valve",
    "mtr": "motor",
    "cntctr": "contactor",
    "aux": "auxiliary"
}}

OCR Text:
{ocr_samples[request_id]}
"""

    return {
        "messages": [
            {
                "role": "system",
                "content": prompt
            }
        ]
    }

def call_api(request_id):
    payload = build_payload(request_id)

    start_timestamp = datetime.now()
    start_time = time.perf_counter()

    logging.info(f"[REQ-{request_id}] Started at {start_timestamp}")
    logging.info(f"[REQ-{request_id}] Payload:\n{payload['messages'][0]['content']}")

    try:
        response = requests.post(
            URL,
            headers=HEADERS,
            json=payload,
            timeout=120
        )

        end_timestamp = datetime.now()
        elapsed = time.perf_counter() - start_time

        logging.info(
            f"[REQ-{request_id}] Completed | "
            f"Status={response.status_code} | "
            f"Response Time={elapsed:.3f}s | "
            f"Completed At={end_timestamp}"
        )

        return {
            "request_id": request_id,
            "status": response.status_code,
            "response_time": round(elapsed, 3),
            "payload": payload,
            "response": response.text
        }

    except Exception as e:
        elapsed = time.perf_counter() - start_time

        logging.error(
            f"[REQ-{request_id}] Failed | "
            f"Response Time={elapsed:.3f}s | Error={str(e)}"
        )

        return {
            "request_id": request_id,
            "status": "FAILED",
            "response_time": round(elapsed, 3),
            "payload": payload,
            "response": str(e)
        }


if __name__ == "__main__":
    total_start = time.perf_counter()

    logging.info(f"Starting {NUM_REQUESTS} parallel API calls...")

    with ThreadPoolExecutor(max_workers=NUM_REQUESTS) as executor:
        futures = [
            executor.submit(call_api, i)
            for i in range(1, NUM_REQUESTS + 1)
        ]

        results = []
        for future in as_completed(futures):
            results.append(future.result())

    total_time = time.perf_counter() - total_start

    print("\n" + "=" * 120)
    print("SUMMARY")
    print("=" * 120)

    for result in sorted(results, key=lambda x: x["request_id"]):
        print(
            f"REQ-{result['request_id']} | "
            f"Status={result['status']} | "
            f"Time={result['response_time']}s"
        )

    print(f"\nTotal Execution Time: {total_time:.3f}s")

    print("\n" + "=" * 120)
    print("RESPONSES")
    print("=" * 120)

    for result in sorted(results, key=lambda x: x["request_id"]):
        print(f"\nREQ-{result['request_id']}")
        print("-" * 120)
        print("Response:")
        print(result["response"])