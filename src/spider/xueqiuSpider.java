package spider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

import util.Util;


public class xueqiuSpider {

	//param数组对应字段
	static int 基金代码 = 0;
	static int 基金名称 = 1;
	static int 持仓股数 = 2;
	static int 占本基金所持流通股比例 = 3;
	static int 持仓变化万股 = 4;
	static int 持仓市值 = 5;
	static int 占净值百分比 = 6;
	static int 占个股流通市值比例 = 7;

	Util util = new Util();

	//获取基金持股数据
	public void getFundholding(String txt) throws IOException, InterruptedException {
		List<String[]> stockNums = util.getStockNum();
		for (int i = 0; i < stockNums.size(); i++) {
			String num = stockNums.get(i)[0];
			String url = "http://stock.jrj.com.cn/share," + num + ",jjcg.shtml";
			String resText = getEntity(url);
			Document doc = Jsoup.parse(resText);
			Elements tableDetail = doc.select("#jjcgTb");
			Elements tableTotal = doc.select(".tab7");
			System.out.println(num);
			if (tableDetail.text().contains("无持股机构")) {
				continue;
			}
			Elements trs = tableDetail.select("tr");
			String[][] param = new String[trs.size() - 1][8];
			for (int j = 1; j < trs.size(); j++) {
				Element tr = trs.get(j);
				param[j - 1] = tr.select("td").text().split(" ");
				param[j - 1][2] = param[j - 1][2].replace(",", "");
				param[j - 1][5] = param[j - 1][5].replace(",", "");
			}

			outputFullFundholding(stockNums.get(i), trs.size() - 1, param, txt);
			Thread.sleep(3000);
		}
	}

	//输出完整的基金持股数据到txt
	//输出格式
	//第1行 股票代码
	//第2行 基金家数
	//第3-n行 基金明细
	public void outputFullFundholding(String[] numAndName, int count, String[][] param, String txt) throws IOException {
		File file = new File(txt);
		if (!file.exists()) {
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(numAndName[0] + "\t" + numAndName[1] + "\r\n");
		writer.write(count + "\r\n");
		for (int i = 0; i < param.length; i++) {
			String[] record = param[i];
			for (int j = 0; j < record.length; j++) {
				String s = record[j];
				writer.write(s + "\t");
			}
			writer.write("\r\n");
		}
		writer.flush();
		writer.close();
	}

	//请求实体
	public String getEntity(String url) throws IOException {
		String cookieStr = "ADVS=35fab31ebfab72; ASL=17585,0000s,df4853e5; vjuids=-3930ad232.161c23e095a.0.29fc3a879486d; bdshare_firstime=1519382301309; Hm_lvt_1d0c58faa95e2f029024e79565404408=1519382102; Hm_lpvt_1d0c58faa95e2f029024e79565404408=1519382491; ADVC=35fab31ebfab72; WT_FPC=id=2bf6546f5ad91bc9b061519382300734:lv=1519382491782:ss=1519382300734; channelCode=3763BEXX; ylbcode=24S2AZ96; Hm_lvt_d654909655f2581e69361531a7850450=1519382301; Hm_lpvt_d654909655f2581e69361531a7850450=1519382492; vjlast=1519382301.1519382301.30; jrj_z3_newsid=8898; jrj_uid=1519382491805LmOqRBuZ6M";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		HttpEntity entity = null;
		Map<String, String> headers = new HashMap<String, String>();
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpGet.setHeader("Upgrade-Insecure-Requests", "1");
		httpGet.setHeader("Cookie", cookieStr);

		httpGet.setHeader("Host", "stock.jrj.com.cn");
		response = httpClient.execute(httpGet);
		entity = response.getEntity();
		String resText = EntityUtils.toString(entity, "GBK");
		return resText;
	}

}

