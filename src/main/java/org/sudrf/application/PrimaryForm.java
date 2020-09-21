/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.application;

import org.sudrf.application.urlSource.FileURLs;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javafx.application.Application;
import static javafx.application.Application.STYLESHEET_MODENA;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.sudrf.application.urlSource.ArbitreURLs;
import org.sudrf.application.urlSource.MagistrateURLs;
import org.sudrf.parsing.CourtsHTMLPaser;

/**
 *
 * @author Kalinin Maksim
 */
public class PrimaryForm extends Application {

    private static final Preferences prefs = Preferences.userNodeForPackage(PrimaryForm.class);

    private File srcFile = new File(prefs.get("srcPath", ""));
    private File destFile = new File(prefs.get("distPath", ""));
    private Stage primaryStage;

    private final MainSceneController mainSceneController = new MainSceneController();
    private final TaskFactory taskFactory = new TaskFactory();
    private final List<ParseService> startedSrvs = new ArrayList<ParseService>();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/rgs/javaFX/primary.fxml"));

            fxmlLoader.setController(mainSceneController);

            Parent primaryScene = fxmlLoader.load();
            Scene scene = new Scene(primaryScene);

            primaryStage.setScene(scene);
            primaryStage.show();

            mainSceneController.updateInfo();

        } catch (IOException ex) {
            Logger.getLogger(PrimaryForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class MainSceneController {

        @FXML
        private CheckBox courtsFromFileChbx, arbitaryCourtsChbx, magistrateCourtsChbx;
        @FXML
        private Button srcFilebtn, destFilebtn, startParsingbtn;
        @FXML
        private ProgressBar magistratePrgBar, arbitaryPrgBar, commonPrgBar;
        @FXML
        private Label commonPrgBrLbl, arbitaryPrgBrLbl, magitratePrgBrLbl;
        @FXML
        private Label srcToDistlbl;
        @FXML
        TextField srcFilePath;

        private void updateInfo() {
            srcFilePath.clear();
            srcFilePath.appendText(srcFile.getPath());

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(destFile == null ? "<директория для сохранения>" : destFile.getAbsolutePath());
            srcToDistlbl.setText(strBuilder.toString());
        }

        @FXML
        private void srcFilebtnOnClick(Event e) {
            FileChooser fileChooser = new FileChooser();
            File initFile = new File(prefs.get("srcPath", ""));
            if (initFile.isFile()) {
                fileChooser.setInitialDirectory(new File(initFile.getParent()));
            }
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel", "*.xls"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                srcFile = file;
                try {
                    prefs.flush();
                    prefs.put("srcPath", srcFile.getPath());
                } catch (BackingStoreException ex) {
                    Logger.getLogger(PrimaryForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateInfo();
            }
        }

        @FXML
        private void destFilebtnOnClick(Event e) {
            DirectoryChooser directoryChooser = new DirectoryChooser();

            File initFile = new File(prefs.get("distPath", ""));
            if (initFile.isDirectory()) {
                directoryChooser.setInitialDirectory(initFile);
            }
            directoryChooser.setTitle("Выбор папки назначения");
            File file = directoryChooser.showDialog(primaryStage);
            if (file != null) {
                destFile = file;
                prefs.put("distPath", destFile.getPath());
                try {
                    prefs.flush();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(PrimaryForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateInfo();
            }
        }

        @FXML
        private void startParsingbtnOnClick() throws InterruptedException {
            List< ParseService> srvList = new ArrayList<ParseService>();
            StringBuilder warnings = new StringBuilder();
            $operation:
            {
                $addTaks:
                {
                    if (courtsFromFileChbx.isSelected()) {
                        if ((srcFile == null) || (destFile == null)) {
                            warnings.append("Должны быть заданы источник адресов и место сохранения");
                            break $addTaks;
                        }
                        ParseService parsingTask = taskFactory.getCommonCourtsTask();
                        commonPrgBar.progressProperty().bind(parsingTask.progressProperty());
                        commonPrgBrLbl.textProperty().bind(parsingTask.messageProperty());
                        srvList.add(parsingTask);
                    }
                }
                $addTaks:
                {
                    if (arbitaryCourtsChbx.isSelected()) {
                        if (destFile == null) {
                            warnings.append("Не задано место сохранения");
                            break $addTaks;
                        }
                        ParseService parsingTask = taskFactory.getArbitaryCourtsTask();
                        arbitaryPrgBar.progressProperty().bind(parsingTask.progressProperty());
                        arbitaryPrgBrLbl.textProperty().bind(parsingTask.messageProperty());
                        srvList.add(parsingTask);

                    }
                }
                $addTaks:
                {
                    if (magistrateCourtsChbx.isSelected()) {
                        if (destFile == null) {
                            warnings.append("Не задано место сохранения");
                            break $addTaks;
                        }
                        ParseService parsingTask = taskFactory.getMagistrateCourtsTask();
                        magistratePrgBar.progressProperty().bind(parsingTask.progressProperty());
                        magitratePrgBrLbl.textProperty().bind(parsingTask.messageProperty());
                        srvList.add(parsingTask);
                    }
                }
                if (srvList.isEmpty()) {
                    warnings.append("Не заданы действия");
                }

                if (warnings.length() != 0) {
                    Util.notifyWarn(warnings.toString());
                    break $operation;
                }
            }

            List<ParseService> allowedSrv = srvList
                    .stream()
                    .filter(((Predicate<ParseService>) ParseService::isRunning).negate())
                    .collect(Collectors.toList());

            for (ParseService parsingSrv : allowedSrv) {
                parsingSrv.start();
                startedSrvs.add(parsingSrv);
            }

        }

        @FXML
        private void cancellParsingbtnOnClick() throws InterruptedException {
            Consumer<ParseService> cancell = ParseService::cancel;
            startedSrvs.forEach(cancell.andThen(ParseService::reset));
            startedSrvs.clear();

        }

    }

    private static class Util {

        private static void notify_(String msg) {
            Alert alert = new Alert(AlertType.NONE, STYLESHEET_MODENA, ButtonType.OK);
            alert.setTitle("Оповещение");
            alert.setContentText(msg);
            alert.showAndWait();
        }

        private static void notifyWarn(String msg) {
            Alert alert = new Alert(AlertType.WARNING, STYLESHEET_MODENA, ButtonType.OK);
            alert.setTitle("Оповещение");
            alert.setContentText(msg);
            alert.showAndWait();
        }

        private static void notifyError(StringBuilder errors) {

            Alert alert = new Alert(AlertType.ERROR, STYLESHEET_MODENA, ButtonType.OK);
            alert.setTitle("Оповещение");
            alert.setContentText(errors.toString());
            alert.showAndWait();
        }

    }

    private class TaskFactory {

        private ParseService commonCourtsService = null;
        private ParseService arbitaryCourtsTask = null;
        private ParseService magistrateCourtsTask = null;

        private ParseService getCommonCourtsTask() {
            if (commonCourtsService == null) {
                commonCourtsService = new ParseService(new ParsingTask(FileURLs::parse), mainSceneController.commonPrgBar);
            }
            return commonCourtsService;
        }

        private ParseService getArbitaryCourtsTask() {
            if (arbitaryCourtsTask == null) {
                arbitaryCourtsTask = new ParseService(new ParsingTask(ArbitreURLs::parse), mainSceneController.arbitaryPrgBar);
            }
            return arbitaryCourtsTask;
        }

        private ParseService getMagistrateCourtsTask() {
            if (magistrateCourtsTask == null) {
                magistrateCourtsTask = new ParseService(new ParsingTask(MagistrateURLs::parse), mainSceneController.magistratePrgBar);
            }
            return magistrateCourtsTask;
        }
    }

    private class ParseService extends Service<ParsingTask.Result> {

        ParsingTask task;
        ProgressBar prgBar;

        public ParseService(ParsingTask task, ProgressBar prgBar) {
            this.task = task;
            this.prgBar = prgBar;

            setOnSucceeded(w -> {
                try {
                    ParsingTask.Result get = task.get();
                    task.cancel();
                    unbindPrgBar();
                    ParseService.this.reset();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PrimaryForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(PrimaryForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            setOnFailed(w -> {
                task.cancel();
                unbindPrgBar();
            });

            setOnCancelled(w -> {
                task.cancel();
                unbindPrgBar();
            });
        }

        @Override
        protected ParsingTask createTask() {
            return task;
        }

        private void unbindPrgBar() {
            prgBar.progressProperty().unbind();
            prgBar.setProgress(0);
        }

    }

    private class ParsingTask extends Task<ParsingTask.Result> {

        private final StringBuilder errors = new StringBuilder();
        private final CourtsHTMLPaser.Manager executor;

        public ParsingTask(CourtsHTMLPaser.Manager executor) {
            this.executor = executor;
        }

        @Override
        protected Result call() throws Exception {
            //throw new ExecutionException("Test", null);
            return success(executor.parse(srcFile, destFile, this::progress, errors));
        }

        public Boolean progress(Double t) {
            updateMessage("Выполняется... ".concat(String.valueOf(t).concat("%")));
            updateProgress(t, 100);
            return !isCancelled();
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            if (!errors.toString().isEmpty()) {
                updateMessage("Ошибка!");
                Util.notifyError(errors);
            } else {
                updateMessage("Завершено");
                Util.notify_("Чтение данных завершено");
            }
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            updateMessage("Отменено!");
        }

        @Override
        protected void failed() {
            super.failed();
            updateMessage("Ошибка!");
        }

        private Result success(String msg) {
            return new Result(msg);
        }

        private class Result {

            String desc;

            public Result(String desc) {
                this.desc = desc;
            }

        }
    }
}
