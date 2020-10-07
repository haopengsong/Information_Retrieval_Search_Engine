import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndex {
    public static Map<String, WordIndexContent> map = new HashMap<String, WordIndexContent>();

    static class WordIndexContent {
        private Map<String, Integer> asset;

        public WordIndexContent() {
            asset = new HashMap<String, Integer>();

        }

        public void insertDocId(String id) {
            Integer i = asset.get(id);
            if (i == null) {
                asset.put(id, 1);
            } else {
                asset.put(id, ++i);
            }
        }

        public String retrieveIndex() {
            StringBuilder sb = new StringBuilder();
            for (String key : asset.keySet()) {
                String count = asset.get(key).toString();
                sb.append(key+":"+count+" ");
            }
            return sb.toString();
        }
    }

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text>{

        //private final static LongWritable one = new LongWritable(1);
        //private static LongWritable docId = new LongWritable();
        private Text word = new Text();
        private static Text docId = new Text();
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String tokenProcessed = "";
            int docIdIndex = value.toString().indexOf('\t');
            if (docIdIndex != -1) {
                String docIdStr = value.toString().substring(0, docIdIndex).trim();
                docId.set(docIdStr);
            } else {
                docId.set("NOT AVAILABLE");
            }
            String contentProcessed = value.toString().replaceAll("[^a-zA-Z]", " ").trim().toLowerCase();
            StringTokenizer itr = new StringTokenizer(contentProcessed);
            while (itr.hasMoreTokens()) {
                //System.out.println(itr.nextToken());
                tokenProcessed = itr.nextToken();
                if (!tokenProcessed.equals("") && !tokenProcessed.equals("\t") && !tokenProcessed.equals(" ")) {
                    word.set(tokenProcessed.trim());
                    context.write(word, docId);
                }
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,Text,Text,Text> {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {

            WordIndexContent wic = new WordIndexContent();
            map.put(key.toString(), wic);
            for (Text val : values) {
                wic.insertDocId(val.toString());
            }
            result.set(wic.retrieveIndex());
            context.write(key, result);
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Inverted Index Phase One");
        job.setJarByClass(InvertedIndex.class);
        job.setMapperClass(TokenizerMapper.class);

        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

