package com.example.jefferson.listadecompas;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.printservice.PrintDocument;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
EditText txtProduto;
ListView minhaLista;
MeuAdapter meuAdapter;
List<Produto> produtos;
int espacador;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printDocument()
    {
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new
                        MyPrintDocumentAdapter(this),
                null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter
    {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 1;

        private void drawPage(PdfDocument.Page page,
                              int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            int titleBaseLine = 72;
            int leftMargin = 180;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            canvas.drawText(
                    "Lista de compras ",
                    leftMargin,
                    titleBaseLine,
                    paint);

            leftMargin = 10;

            paint.setTextSize(14);
            ListaDAO dao = new ListaDAO(MainActivity.this);
            produtos = dao.getLista();
            dao.close();

    for(int i = 0;i <= produtos.size()-1;i++){
        if (i==0){
            espacador = 35;
        }else{
            espacador = espacador+ 20;
        }if(espacador > 715){
            espacador = 35;
            leftMargin = leftMargin + 200;
        }



        canvas.drawText(produtos.get(i).getProduto(), leftMargin, titleBaseLine + espacador, paint);
    }


            PdfDocument.PageInfo pageInfo = page.getInfo();


        }

        public MyPrintDocumentAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }


        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }
        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListaDAO dao = new ListaDAO(MainActivity.this);




       minhaLista = findViewById(R.id.listView);

       minhaLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setTitle("Remover!")
                       .setMessage("Deseja remover este item?")
                       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               ListaDAO dao = new ListaDAO(MainActivity.this);
                               dao.apagarProduto(produtos.get(position));
                               produtos.remove(position);

                               meuAdapter.notifyDataSetChanged();
                           }
                       })
                       .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });

               Dialog dialog = builder.create();
               dialog.show();

           }
       });

        txtProduto = findViewById(R.id.edtProduto);
        txtProduto.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                        adicionar();
                        return true;
                    }

                }
                return false;
            }
        });

        carregaLista();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
                adicionar();

            return true;
        }
        if (id == R.id.Deletar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Apagar!")
                    .setMessage("Deseja apagar todos os itens?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListaDAO dao = new ListaDAO(MainActivity.this);
                            dao.deletaTabela();
                            meuAdapter.notifyDataSetChanged();
                            carregaLista();

                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            Dialog dialog = builder.create();
            dialog.show();

        }
        if (id == R.id.Imprimir) {
            printDocument();
        }


        return super.onOptionsItemSelected(item);
    }

    private void carregaLista() {
        ListaDAO dao = new ListaDAO(this);
        produtos = dao.getLista();

        dao.close();
        meuAdapter = new MeuAdapter(this, produtos);

        this.minhaLista.setAdapter(meuAdapter);
    }
    private void adicionar(){
        if (txtProduto.getText().toString().isEmpty()){

            Toast.makeText(this,"Informe o item a ser inserido",Toast.LENGTH_SHORT).show();

        }else {
                if (produtos.size()-1 >= 105){
                    Toast.makeText(this,"Numero maximo de produtos atingido",Toast.LENGTH_SHORT).show();
                }else {
                    Produto p = new Produto();
                    p.setId("0");
                    p.setProduto(txtProduto.getText().toString());
                    Log.d("meuLog", "Produto: " + p.produto);
                    ListaDAO dao = new ListaDAO(this);
                    Log.d("meuLog", "ListaDAO ");
                    dao.inserirProduto(p);
                    Log.d("meuLog", "Inseriu produto ");
                    meuAdapter.notifyDataSetChanged();
                    Log.d("meuLog", "Carregou meu adapter ");
                    carregaLista();

                    txtProduto.setText("");
                }

        }
    }



}
